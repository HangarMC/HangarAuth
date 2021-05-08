package io.papermc.hangarauth.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HtmlResourceView extends AbstractUrlBasedView {

    HtmlResourceView() {

    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(this.getContentType());
        String url = getUrl();
        if (url == null) {
            throw new RuntimeException("No url?");
        }
        ClassPathResource classPathResource = new ClassPathResource(url);
        if (!classPathResource.isFile() || !classPathResource.exists()) {
            throw new RuntimeException("Not a file " + url);
        }
        FileCopyUtils.copy(classPathResource.getInputStream(), response.getOutputStream());
    }
}
