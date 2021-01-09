package com.xx.hotfixdemo

import android.content.Context
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

class HotFixSDK(private val context: Context) {
    fun install() {
        val pathLoader = context.classLoader
        //获取BaseDexClassloader中的pathList变量
        val pathListField = pathLoader.javaClass.superclass?.getDeclaredField("pathList")
        pathListField?.apply {
            if (!isAccessible) {
                isAccessible = true
                //获取PathList对应的类对象
                val pathList = get(context.classLoader)
                //获取PathList中的Element变量
                val dexElementsField = pathList.javaClass.getDeclaredField("dexElements")
                if (!dexElementsField.isAccessible) {
                    dexElementsField.isAccessible = true
                }
                val dexElements = dexElementsField.get(pathList) as Array<Any>
                //从sd卡中获取dex文件
                val file = File("sdcard/fix.dex")
                if (!file.exists()) {
                    return
                }
                val list = ArrayList<File>()
                list.add(file)
                val exceptions = ArrayList<IOException>()
                //将新的dex转换为dexElements数组
                val makePathElements =
                    makePathElements(pathList, list, context.cacheDir, exceptions)
                val newElements = java.lang.reflect.Array.newInstance(
                    dexElements.javaClass.componentType,
                    dexElements.size + makePathElements.size
                )
                //将新的dexElements和旧的dexElement数组合并
                System.arraycopy(makePathElements, 0, newElements, 0, makePathElements.size)
                System.arraycopy(
                    dexElements,
                    0,
                    newElements,
                    makePathElements.size,
                    dexElements.size
                )
                //将合并后的dexElements数组重新设置给DexPathList中的dexElements
                dexElementsField.set(pathList, newElements)
            }
        }
    }

    /**
     * 将dex转化为Element数组
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(
        IllegalAccessException::class,
        InvocationTargetException::class,
        NoSuchMethodException::class
    )
    private fun makePathElements(
        dexPathList: Any,
        files: ArrayList<File>,
        optimizedDirectory: File,
        exceptions: ArrayList<IOException>
    ): Array<Any> {
        val makePathElementsMethod = dexPathList.javaClass.getDeclaredMethod(
            "makePathElements",
            List::class.java,
            File::class.java,
            List::class.java
        )
        if (!makePathElementsMethod.isAccessible) {
            makePathElementsMethod.isAccessible = true
        }
        return makePathElementsMethod.invoke(
            dexPathList,
            files,
            optimizedDirectory,
            exceptions
        ) as Array<Any>
    }
}