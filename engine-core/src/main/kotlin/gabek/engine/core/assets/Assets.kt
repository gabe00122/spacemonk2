package gabek.engine.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.MusicLoader
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.utils.*
import gabek.engine.core.audio.SoundRef
import gabek.engine.core.graphics.Animation
import gabek.engine.core.graphics.Sprite
import ktx.log.logger

/**
 * @author Gabriel Keith
 */
class Assets: Disposable {
    companion object {
        private val log = logger<Assets>()
    }

    private val resourceManager = AssetManager()

    private val textureMap = HashMap<String, Sprite>()
    private val shaderMap = HashMap<String, ShaderProgram>()
    private val animationMap = HashMap<String, Animation>()
    private val musicMap = HashMap<String, Music>()
    private val soundMap = HashMap<String, SoundRef>()

    constructor(root: String = "assets", configOnly: Boolean = true) {
        initLoaders()

        loadAssetDir(root)
        if (!configOnly) {
            finish()
        }
    }

    fun update(): Boolean = resourceManager.update()

    val progress: Float
        get() = resourceManager.progress

    fun finish() {
        resourceManager.logger.level = Logger.INFO
        resourceManager.finishLoading()
    }

    private fun initLoaders() {
        resourceManager.setLoader(AnimationMap::class.java, AnimationLoader(this, resourceManager.fileHandleResolver))
    }

    fun loadAssetDir(dir: String) {
        ScanningAssetDirector(resourceManager.fileHandleResolver.resolve(dir)).configure(this)
    }


    fun loadTexturePack(packName: String, fileName: String) {
        val cacheName = "cache"
        val atlasName = "./$cacheName/$packName.atlas"

        val settings = TexturePacker.Settings()
        settings.stripWhitespaceX = true
        settings.stripWhitespaceY = true
        settings.duplicatePadding = true
        TexturePacker.processIfModified(settings, fileName, "./$cacheName", packName)


        val parameter = TextureAtlasLoader.TextureAtlasParameter()
        parameter.loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, name, _ ->
            val atlas = assetManager.get(name, com.badlogic.gdx.graphics.g2d.TextureAtlas::class.java)

            for (region in atlas.regions) {
                val lookup = "$packName:${region.name}:${region.index}"

                textureMap.put(lookup, Sprite(lookup, region,
                        region.originalWidth / 2f - (region.offsetX + region.packedWidth / 2f),
                        -region.originalHeight / 2f + ((region.offsetY) + region.packedHeight / 2f)))
            }
        }

        resourceManager.load(atlasName, TextureAtlas::class.java, parameter)
    }


    /*
    fun loadTexturePack(packName: String, fileName: String){
        val file = resourceManager.fileHandleResolver.resolve(fileName)

        val images = Array<Pixmap>()
        val imageNames = ArrayMap<Pixmap, String>()
        for(f in file.list()){
            if(f.extension() == "png") {
                val image = Pixmap(f)
                images.add(image)
                imageNames.put(image, f.nameWithoutExtension())
            }
        }

        val packer = PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 2, true)
        packer.sort(images)

        images.forEach { packer.pack(imageNames[it], it) }

        val atlas = packer.generateTextureAtlas(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest,
                false)

        PixmapPackerIO().save(Gdx.files.local("./test"), packer)
        packer.dispose()
        images.forEach { it.dispose() }

        for (region in atlas.regions) {
            val lookup = "$packName:${region.name}:${region.index}"

            textureMap.put(lookup, Sprite(lookup, region,
                    region.originalWidth/2f - (region.offsetX + region.packedWidth/2f),
                    -region.originalHeight/2f + (region.offsetY + region.packedHeight/2f)))
        }
    }
    */

    fun loadShader(shaderName: String, vertFile: String, fragFile: String) {
        val parameter = ShaderProgramLoader.ShaderProgramParameter()
        parameter.vertexFile = vertFile
        parameter.fragmentFile = fragFile

        parameter.loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, type ->
            val shaderProgram = assetManager.get(fileName, ShaderProgram::class.java)
            if(!shaderProgram.isCompiled){
                log.error { "Failed to compile shader\n" }
            }
            log.debug { shaderProgram.log }
            shaderMap.put(shaderName, shaderProgram)
        }

        resourceManager.load(shaderName, ShaderProgram::class.java, parameter)
    }

    fun loadMusic(musicName: String, fileName: String) {
        val parameter = MusicLoader.MusicParameter()
        parameter.loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, type ->
            val music = assetManager.get(fileName, Music::class.java)
            musicMap.put(musicName, music)
        }

        resourceManager.load(fileName, Music::class.java, parameter)
    }

    fun loadSound(soundName: String, fileName: String){
        val parameter = SoundLoader.SoundParameter()
        parameter.loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, type ->
            val sound = assetManager.get(fileName, Sound::class.java)
            soundMap.put(soundName, SoundRef(sound, soundName))
        }

        resourceManager.load(fileName, Sound::class.java, parameter)
    }

    fun loadAnimationPack(packName: String, file: String) {
        val param = gabek.engine.core.assets.AnimationLoader.Parameters()

        param.loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, type ->
            val aMap = assetManager.get(file, AnimationMap::class.java)
            for (anim in aMap) {
                animationMap.put("$packName:${anim.key}", anim.value)
            }
        }

        resourceManager.load(file, AnimationMap::class.java, param)
    }

    fun getTexture(lookup: String): Sprite {
        return textureMap[defaultIndex(lookup)]!!
    }

    fun getTextures(lookup: String, range: IntRange): List<Sprite> {
        return range.map { getTexture("$lookup:$it") }
    }

    fun getTextures(lookup: String): List<Sprite> {
        val base = lookup.replace(":*", "")
        val out = mutableListOf<Sprite>()

        var index = 0
        var current = textureMap["$base:${index++}"]

        while (current != null) {
            out.add(current)
            current = textureMap["$base:${index++}"]
        }

        return out
    }

    private fun defaultIndex(lookup: String): String {
        if (lookup.count { it == ':' } < 2) {
            return "$lookup:-1"
        } else {
            return lookup
        }
    }

    fun getAnimation(lookup: String): Animation {
        return animationMap[lookup]!!
    }

    fun getMusic(lookup: String): Music {
        return musicMap[lookup]!!
    }

    fun getSound(lookup: String): SoundRef {
        return soundMap[lookup]!!
    }

    fun getShader(lookup: String): ShaderProgram {
        return shaderMap[lookup]!!
    }

    override fun dispose() {
        resourceManager.dispose()
    }
}