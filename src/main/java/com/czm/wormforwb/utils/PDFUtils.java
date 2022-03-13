package com.czm.wormforwb.utils;


import com.alibaba.fastjson.JSONObject;
import com.czm.wormforwb.pojo.vo.DynamicResVO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * PDF工具类
 * @author Slience
 * @date 2022/3/11 16:34
 **/
public class PDFUtils {

    /**
     * 初始化PDF文件类Document
     * @param pdfPath pdf文件路径包括文件名
     * @return Document PDF文件类型
     **/
    public static Document createNewPDF(String pdfPath) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(pdfPath);
        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // 3、Adding an empty page
        pdfDoc.addNewPage();
        // 4、Creating a Document
        Document document = new Document(pdfDoc);
        return document;
    }

    /**
     * 向指定PDF文件写入动态内容
     * @param document 指定PDF文件
     * @param dynamicResVOList 动态内容
     **/
    public static void writeDynamicContentToPDF(Document document, List<DynamicResVO> dynamicResVOList){
        for(DynamicResVO dynamicResVO : dynamicResVOList){

        }
        document.close();
    }

}
