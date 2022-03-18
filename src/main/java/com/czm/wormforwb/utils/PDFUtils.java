package com.czm.wormforwb.utils;

import com.czm.wormforwb.pojo.vo.DynamicResExtVO;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF工具类
 * @author Slience
 * @date 2022/3/11 16:34
 **/
@Slf4j
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
        return new Document(pdfDoc);
    }

    /**
     * 向指定PDF文件写入动态内容
     * @param document 指定PDF文件
     * @param dynamic 动态内容
     **/
    public static void writeDynamicContentToPDF(Document document, DynamicResExtVO dynamic){
        //标题内容
        document.add(getTitle(dynamic.getName(), dynamic.getCreateTime()));
        //正文内容
        document.add(getContent(dynamic.getText()));
        //超链接
        document.add(getLink(dynamic.getPageUrl()));
        if(dynamic.getPics().size() == 1){
            document.add(getSingleImage(dynamic.getPics().get(0)));
        } else if(dynamic.getPics().size() > 1){
            document.add(getMultiImage(dynamic.getPics()));
        }
    }

    private static Paragraph getTitle(String name, String createTime){
        Text t1 = new Text(name + "\t" + createTime);
        t1.addStyle(getTitleStyle());
        return new Paragraph(t1);
    }

    /**
     * 根据文本内容生成文本实体
     * @param content 文本内容
     * @return Text PDF文本类型
     **/
    private static Paragraph getContent(String content){
        Text t1 = new Text(content.replaceAll("<[^>]+>",""));
        t1.addStyle(getContentStyle());
        return new Paragraph(t1);
    }

    /**
     * 根据url生成超链接实体
     * @param url 超链接url
     * @return Link PDF超链接类型
     **/
    private static Paragraph getLink(String url){
        Rectangle rect = new Rectangle(0, 0);
        PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
        PdfAction action = PdfAction.createURI(url);
        annotation.setAction(action);
        Link link = new Link("点我康好康的～", annotation);
        link.addStyle(getLinkStyle());
        link.setUnderline();
        return new Paragraph(link);
    }

    /**
     * 根据图片url生成单元格用于多图显示
     * @param imageUrl 图片url
     * @return Cell PDF表格单元格类型
     **/
    private static Cell getCellImage(String imageUrl){
        try{
            ImageData data = ImageDataFactory.create(imageUrl);
            Image image = new Image(data);
            image.setWidth(150);
            image.setHeight(150);
            return new Cell().add(image).setBorder(Border.NO_BORDER);
        }catch (MalformedURLException e){
            log.error("PDF工具类生成图片抛出异常：{}", e.getStackTrace() );
            return new Cell();
        }
    }

    /**
     * 生成多图组合,默认一行3列
     * @param picList 图片url列表
     * @return Table PDF表格类型
     **/
    private static Table getMultiImage(List<String> picList){
        List<Cell> cellList = new ArrayList<>();
        for(String pic : picList){
            cellList.add(getCellImage(pic));
        }
        float [] pointColumnWidths = {150f, 150f, 150f};
        Table table = new Table(pointColumnWidths);
        table.setBorder(Border.NO_BORDER);
        for(Cell cell : cellList){
            table.addCell(cell);
        }
        return table;
    }

    /**
     * 根据图片url生成图片实体
     * @param imageUrl 图片url
     * @return Image PDF图片类型
     **/
    private static Image getSingleImage(String imageUrl){
        try{
            ImageData data = ImageDataFactory.create(imageUrl);
            Image image = new Image(data);
            image.setAutoScale(true);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image;
        }catch (MalformedURLException e){
            log.error("PDF工具类生成图片抛出异常：{}", e.getStackTrace() );
            return null;
        }
    }

    /**
     * 文本正文样式
     * @return Style 文本样式
     **/
    private static Style getContentStyle(){
        Style style = new Style();
        try{
            PdfFont pdfFont = PdfFontFactory.createFont("fonts/simsun.ttc,1", PdfEncodings.IDENTITY_H);
            style.setFont(pdfFont);
            return style;
        } catch (IOException e){
            log.error("PDF工具类字体样式抛出异常：{}", e.getStackTrace());
        }
        return style;
    }

    /**
     * 获取超链接文本样式
     * @return Style 文本样式
     */
    private static Style getLinkStyle(){
        Style style = new Style();
        try {
            PdfFont pdfFont = PdfFontFactory.createFont("fonts/simsun.ttc,1", PdfEncodings.IDENTITY_H);
            style.setFont(pdfFont).setFontColor(DeviceRgb.BLUE);
        } catch (IOException e){
            log.error("PDF工具类字体样式抛出异常：{}", e.getStackTrace());
        }
        return style;
    }

    private static Style getTitleStyle(){
        Style style = new Style();
        try {
            PdfFont pdfFont = PdfFontFactory.createFont("fonts/simhei.ttc,1", PdfEncodings.IDENTITY_H);
            style.setFont(pdfFont);
        } catch (IOException e){
            log.error("PDF工具类字体样式抛出异常：{}", e.getStackTrace());
        }
        return style;
    }

}
