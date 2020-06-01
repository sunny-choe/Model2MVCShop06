package com.model2.mvc.view.product;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddProductAction extends Action{

	@Override
	public String execute(HttpServletRequest request,
							HttpServletResponse response) throws Exception{
		
		if (FileUpload.isMultipartContent(request)) {
			//Eclipse workspace / project 변경 시 변경할 것
			// String temDir = "D:\\workspace03(Project&analysis)\\
			//9941.1.Model2MVCShop(ins)\\WebContent\\images\\uploadFiles\\";
			
			String temDir = "C:\\workspace03(Project&analysis)\\"
					+ "9941.1.Model2MVCShop(ins)\\WebContent\\images\\uploadFiles\\";
			//String temDir = ".";
			//String temDir2 = "/uploadFiles/";
			
			DiskFileUpload fileUpload = new DiskFileUpload();
			fileUpload.setRepositoryPath(temDir);
			//setSize Threshold의 크기를 벗어나게 되면 지정한 위치에 임시로 저장
			fileUpload.setSizeMax(1024*1024*10);
			//최대 1메가 까지 업로드 가능 : 1024*1024*10==100MB
			fileUpload.setSizeThreshold(1024*100);
			
			if(request.getContentLength() < fileUpload.getSizeMax()) {
				Product product = new Product();
				
				StringTokenizer token = null;
				
				//parseRequest()는 FileItem을 포함하고 있는 List타입을 리턴한다
				List fileItemList = fileUpload.parseRequest(request);
				int Size = fileItemList.size();
				for(int i = 0; i < Size; i++) {
					FileItem fileItem = (FileItem) fileItemList.get(i);
					//isFormField()를 통해서 파일형식인지 파라미터인지 구분한다. 파라미터라면 true
					if(fileItem.isFormField()) {
						if(fileItem.getFieldName().equals("manuDate")) {
							token = new StringTokenizer(fileItem.getString("euc-kr"),"-");
							String manuDate = token.nextToken() + token.nextToken() + 
									token.nextToken();
							product.setManuDate(manuDate);
						}
						else if(fileItem.getFieldName().equals("prodName"))
							product.setProdName(fileItem.getString("euc-kr"));
						else if(fileItem.getFieldName().equals("prodDetail"))
							product.setProdDetail(fileItem.getString("euc-kr"));
						else if(fileItem.getFieldName().equals("price"))
							product.setPrice(Integer.parseInt(fileItem.getString("euc-kr")));
					}else {
						//out.print("파일 : "+fileItem.getFieldName()+" = "+fileItem.getName();
						//out.print("("+fileItem.getSize()+"byte)<br>");
						
						if (fileItem.getSize() > 0) { //파일을 저장하는 if
							int idx = fileItem.getName().lastIndexOf("\\");
							// getName()은 경로를 다 가져오기 때문에 lastIndexOf로 잘라낸다
							if (idx==1) {
								idx = fileItem.getName().lastIndexOf("/");
							}
							String fileName = fileItem.getName().substring(idx+1);
							product.setFileName(fileName);
							try {
								File uploadedFile = new File(temDir, fileName);
								fileItem.write(uploadedFile);
							} catch (IOException e) {
								System.out.println(e);
							}
						} else {
							product.setFileName("../../images/empty.GIF");
						}
					}
				}//for
				
				ProductServiceImpl service = new ProductServiceImpl();
				service.addProduct(product);
				
				request.setAttribute("product", product);
			} else { //업로드하는 파일이 setSizeMax보다 큰 경우
				int overSize = (request.getContentLength() / 1000000);
				System.out.println("<script>alert('파일의 크기는 1MB까지 입니다."
						+ "올리신 파일의 용량은 "+overSize+"MB입니다.');");
			}
		} else {
			System.out.println("인코딩 타입이 multipart/form-data가 아닙니다.");
		}
		
		return "forward:/product/getProduct.jsp";
	}

}

