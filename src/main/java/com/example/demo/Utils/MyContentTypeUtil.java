package com.example.demo.Utils;

public enum MyContentTypeUtil {
    HTML("text/html;charset=UTF-8"),
    PLAIN("text/plain;charset=UTF-8"),
    MP3("audio/mp3"),
    MP4("audio/mp4"),
    XML_TEXT("text/xml"),
    GIF("image/gif"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    XHTML("application/xhtml+xml"),
    XML_APPLICATION("application/xml"),
    ATOMXML("application/atom+xml"),
    JSON("application/json"),
    PDF("application/pdf"),
    WORD("application/msword"),
    //二进制数据流，文件下载
    DOWLOAD("application/octet-stream"),
    X_WWW_FORM_URLENCODE("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data");
    String contentType;
     MyContentTypeUtil(String contentType)
    {
        this.contentType=contentType;
    }
    public String getContentType()
    {
        return contentType;
    }
}
