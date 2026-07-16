# Cómo traer imágenes de Bittick Agents desde ordinals.com

## URL base

```
https://ordinals.com/content/{inscriptionId}
```

Retorna los bytes PNG directamente. No requiere API key. No requiere autenticación.

---

## Los 100 IDs de Bittick Agents

```
ef7563ebd206be7271685774b39eec7c188ff57f763e08b31e84732848c8101bi0
633d5ed3dd194f7a185ca30a509974a1933a0e84db989298c1ee092ac810db36i0
d5f21c0c8a4661f596f740203aeb934eee72f155277189dab5797b0864c6439fi0
6591e7240a5bb191055dc8e0ae4d81f7e4f933b85d6dc89c7904f9e45a513674i0
c31c1d04b68171b5405aea66f62111efd3a1c47cfbfcae8253146e448905fc50i0
09fef6597c5206b97372cedace97364f078b61ef468a0c2ef6b11e82df0995aci0
ac7196dff767bfc870213adb557169984bc0c749452089cbceb1d5b6e395d95ci0
3e405acb3d046a38e8c31c99ccbee3b12221f3747fc0c5e3601997937a311b8ai0
243f265f151bd3a68a96976bd51e9da5027ad5e3908e6d5fcbd6b4ec06e2ef59i0
6355c18aa385c2d53c04e393a4ce0898f2add7aa99f9e0d03d299879b0fdb01ai0
7a43b6c2c2129871bed2e1b34c766906ac407b0290c8d80353e2af19d7224d34i0
689556d4a1cabce4b4aed38dd92297300103d0e3232661a30c768b8e665a82e3i0
d0d9ff1321be18a87f26dbf559018a0a53b712a3aed2d5bfe185241d106b32eei0
99a721de93233abf5d3422514ad558dbdc84188e24dcf7afbb44ef754c1722edi0
503874ef280d937f5ca003605a6991051a4a99ab0a07aab683c2ecf8d57cd4e1i0
e949b589cbda4717cbadf4038fe593d1c951a0ff63916311a024b11917fed419i0
8ce536a14c2eee4a93d03c830aa120fcee2d5f393b112657b40605e2aefe02f9i0
```

(Faltan 83 IDs — el archivo completo está en `BittickAgentIds.kt` del proyecto BitmapCore)

---

## Código Kotlin para traer UNA imagen

```kotlin
import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

fun fetchBittickImage(inscriptionId: String): android.graphics.Bitmap? {
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val request = Request.Builder()
        .url("https://ordinals.com/content/$inscriptionId")
        .get()
        .build()

    val response = client.newCall(request).execute()
    val bytes = response.body?.bytes() ?: return null

    if (bytes.isEmpty()) return null

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
```

---

## Traer las 100 imágenes en paralelo (rápido)

Descargar 10 a la vez con `Semaphore` para no saturar ordinals.com:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

suspend fun fetchAllBittickImages(
    ids: List<String>
): Map<String, android.graphics.Bitmap> = coroutineScope {

    val semaphore = Semaphore(10) // máximo 10 descargas concurrentes
    val results = mutableMapOf<String, android.graphics.Bitmap>()

    ids.map { id ->
        async(Dispatchers.IO) {
            semaphore.withPermit {
                try {
                    val bitmap = fetchBittickImage(id)
                    if (bitmap != null) {
                        synchronized(results) {
                            results[id] = bitmap
                        }
                    }
                    delay(50) // pausa 50ms entre descargas para no bloquear
                } catch (e: Exception) {
                    // ignorar errores individuales
                }
            }
        }
    }.awaitAll()

    results
}
```

---

## Guardar en SharedPreferences de la app

Las imágenes se guardan como **Base64** en SharedPreferences:

```kotlin
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun saveBittickImageToPrefs(
    context: Context,
    inscriptionId: String,
    bitmap: Bitmap
) {
    val prefs = context.getSharedPreferences("bittick_images", Context.MODE_PRIVATE)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    prefs.edit().putString(inscriptionId, base64).apply()
}

fun loadBittickImageFromPrefs(
    context: Context,
    inscriptionId: String
): Bitmap? {
    val prefs = context.getSharedPreferences("bittick_images", Context.MODE_PRIVATE)
    val base64 = prefs.getString(inscriptionId, null) ?: return null
    val bytes = Base64.decode(base64, Base64.NO_WRAP)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
```

---

## Flujo completo

1. Al conectar wallet → obtener lista de inscripciones del usuario
2. Filtrar las que están en la lista de 100 IDs de Bittick Agents
3. Para cada una que NO esté en SharedPreferences → descargar de `ordinals.com/content/{id}`
4. Guardar en SharedPreferences como Base64
5. Para mostrar → leer de SharedPreferences (instantáneo, sin red)

---

## Notas importantes

- ordinals.com no tiene API key ni rate limit documentado, pero 10 concurrentes es seguro
- Las imágenes son PNG de ~200x200px, pesan entre 5KB y 50KB cada una
- SharedPreferences soporta hasta ~1MB por archivo, 100 imágenes de 50KB = ~5MB total. Si excede, usar Room o caché en disco
- La primera carga tarda ~5-10 segundos. Después es instantáneo desde caché
