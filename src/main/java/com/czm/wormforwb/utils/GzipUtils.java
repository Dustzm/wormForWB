package com.czm.wormforwb.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 文件压缩工具类
 * @author Slience
 * @date 2022/3/12 20:56
 **/
@Component
public class GzipUtils {

    //统一日志压缩路径
    private static String unifiedGzipPath;

    /**
     * 压缩指定文件夹到指定位置
     * @param sourcePath 源文件夹
     * @param outputName 目标tar文件名
     **/
    public static void testDirTarGzip(String sourcePath, String outputName) throws IOException {
        // 被压缩打包的文件夹
        Path source = Paths.get(sourcePath);
        //如果不是文件夹抛出异常
        if (!Files.isDirectory(source)) {
            throw new IOException("请指定一个文件夹");
        }
        //压缩之后的输出文件名称
        StringBuilder tarFileName = new StringBuilder(unifiedGzipPath);
        if(!unifiedGzipPath.endsWith("/")){
            tarFileName.append("/");
        }
        tarFileName.append(outputName).append(".tar.gz");
        //OutputStream输出流、BufferedOutputStream缓冲输出流
        //GzipCompressorOutputStream是gzip压缩输出流
        //TarArchiveOutputStream打tar包输出流（包含gzip压缩输出流）
        try (OutputStream fOut = Files.newOutputStream(Paths.get(tarFileName.toString()));
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {
            //遍历文件目录树
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                //当成功访问到一个文件
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attributes) throws IOException {

                    // 判断当前遍历文件是不是符号链接(快捷方式)，不做打包压缩处理
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    //获取当前遍历文件名称
                    Path targetFile = source.relativize(file);
                    //将该文件打包压缩
                    TarArchiveEntry tarEntry = new TarArchiveEntry(
                            file.toFile(), targetFile.toString());
                    tOut.putArchiveEntry(tarEntry);
                    Files.copy(file, tOut);
                    tOut.closeArchiveEntry();
                    //继续下一个遍历文件处理
                    return FileVisitResult.CONTINUE;
                }
                //当前遍历文件访问失败
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.printf("无法对该文件压缩打包为tar.gz : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
            //for循环完成之后，finish-tar包输出流
            tOut.finish();
        }
    }

    @Value("${unified.gzip.path}")
    private void setUnifiedGzipPath(String unifiedGzipPath){
        GzipUtils.unifiedGzipPath = unifiedGzipPath;
    }

}
