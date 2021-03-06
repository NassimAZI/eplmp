/*******************************************************************************
  * Copyright (c) 2017-2019 DocDoku.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    DocDoku - initial API and implementation
  *******************************************************************************/

package org.polarsys.eplmp.server.rest.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.polarsys.eplmp.core.common.BinaryResource;
import org.polarsys.eplmp.core.document.DocumentMasterTemplateKey;
import org.polarsys.eplmp.core.services.IBinaryStorageManagerLocal;
import org.polarsys.eplmp.core.services.IDocumentManagerLocal;
import org.polarsys.eplmp.core.services.IOnDemandConverterManagerLocal;
import org.polarsys.eplmp.server.util.PartImpl;
import org.polarsys.eplmp.server.util.ResourceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.mockito.MockitoAnnotations.initMocks;

public class DocumentTemplateBinaryResourceTest {
    @InjectMocks
    DocumentTemplateBinaryResource documentTemplateBinaryResource = new DocumentTemplateBinaryResource();
    @Mock
    private IBinaryStorageManagerLocal storageManager;
    @Mock
    private IDocumentManagerLocal documentService;
    @Mock
    private IOnDemandConverterManagerLocal onDemandConverterManager;
    @Spy
    BinaryResource binaryResource;

    @Before
    public void setup() throws Exception {
        initMocks(this);
    }

    @Test
    public void downloadDocumentTemplateFile() throws Exception {
        Request request = Mockito.mock(Request.class);

        binaryResource = Mockito.spy(new BinaryResource(ResourceUtil.FILENAME1, ResourceUtil.DOCUMENT_SIZE, new Date()));

        String fullName = ResourceUtil.WORKSPACE_ID + "/document-templates/" + ResourceUtil.DOC_TEMPLATE_ID + "/" + ResourceUtil.FILENAME1;
        Mockito.when(documentService.getTemplateBinaryResource(fullName)).thenReturn(binaryResource);
        URL resource = getClass().getClassLoader().getResource(ResourceUtil.SOURCE_FILE_STORAGE + ResourceUtil.FILENAME1);
        Assert.assertNotNull(resource);
        File input = new File(resource.getFile());
        FileInputStream fileInputStream = new FileInputStream(input);
        Mockito.when(storageManager.getBinaryResourceInputStream(binaryResource)).thenReturn(fileInputStream);
        //When
        Response response = documentTemplateBinaryResource.downloadDocumentTemplateFile(request, ResourceUtil.RANGE, ResourceUtil.WORKSPACE_ID, ResourceUtil.DOC_TEMPLATE_ID, ResourceUtil.FILENAME1, ResourceUtil.FILE_TYPE, null);

        //Then
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), 206);
        Assert.assertEquals(response.getStatusInfo(), Response.Status.PARTIAL_CONTENT);
        Assert.assertNotNull(response.getEntity());

    }

    @Test
    public void downloadDocumentTemplateFiles() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        binaryResource = Mockito.spy(new BinaryResource(ResourceUtil.FILENAME1, ResourceUtil.DOCUMENT_SIZE, new Date()));
        Collection<Part> filesParts = new ArrayList<>();
        URL resource = getClass().getClassLoader().getResource(ResourceUtil.SOURCE_FILE_STORAGE + ResourceUtil.FILENAME1);
        Assert.assertNotNull(resource);
        filesParts.add(new PartImpl(new File(resource.getFile())));
        File uploadedFile = File.createTempFile(ResourceUtil.TARGET_FILE_STORAGE + "new_" + ResourceUtil.FILENAME1, ResourceUtil.TEMP_SUFFIX);

        OutputStream outputStream = new FileOutputStream(uploadedFile);
        Mockito.when(documentService.saveFileInTemplate(Matchers.any(DocumentMasterTemplateKey.class), Matchers.anyString(), Matchers.anyInt())).thenReturn(binaryResource);
        Mockito.when(storageManager.getBinaryResourceOutputStream(binaryResource)).thenReturn(outputStream);
        Mockito.when(request.getRequestURI()).thenReturn(ResourceUtil.WORKSPACE_ID + "/documents/" + ResourceUtil.DOCUMENT_ID + "/" + ResourceUtil.FILENAME1);
        Mockito.when(request.getParts()).thenReturn(filesParts);

        //When
        Response response = documentTemplateBinaryResource.uploadDocumentTemplateFiles(request, ResourceUtil.WORKSPACE_ID, ResourceUtil.DOC_TEMPLATE_ID);

        //Then
        Assert.assertNotNull(response);
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Assert.assertNotNull(response.getLocation());

        //delete temporary file
        uploadedFile.deleteOnExit();


    }
}
