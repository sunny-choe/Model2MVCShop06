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
			//Eclipse workspace / project ���� �� ������ ��
			// String temDir = "D:\\workspace03(Project&analysis)\\
			//9941.1.Model2MVCShop(ins)\\WebContent\\images\\uploadFiles\\";
			
			String temDir = "C:\\workspace03(Project&analysis)\\"
					+ "9941.1.Model2MVCShop(ins)\\WebContent\\images\\uploadFiles\\";
			//String temDir = ".";
			//String temDir2 = "/uploadFiles/";
			
			DiskFileUpload fileUpload = new DiskFileUpload();
			fileUpload.setRepositoryPath(temDir);
			//setSize Threshold�� ũ�⸦ ����� �Ǹ� ������ ��ġ�� �ӽ÷� ����
			fileUpload.setSizeMax(1024*1024*10);
			//�ִ� 1�ް� ���� ���ε� ���� : 1024*1024*10==100MB
			fileUpload.setSizeThreshold(1024*100);
			
			if(request.getContentLength() < fileUpload.getSizeMax()) {
				Product product = new Product();
				
				StringTokenizer token = null;
				
				//parseRequest()�� FileItem�� �����ϰ� �ִ� ListŸ���� �����Ѵ�
				List fileItemList = fileUpload.parseRequest(request);
				int Size = fileItemList.size();
				for(int i = 0; i < Size; i++) {
					FileItem fileItem = (FileItem) fileItemList.get(i);
					//isFormField()�� ���ؼ� ������������ �Ķ�������� �����Ѵ�. �Ķ���Ͷ�� true
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
						//out.print("���� : "+fileItem.getFieldName()+" = "+fileItem.getName();
						//out.print("("+fileItem.getSize()+"byte)<br>");
						
						if (fileItem.getSize() > 0) { //������ �����ϴ� if
							int idx = fileItem.getName().lastIndexOf("\\");
							// getName()�� ��θ� �� �������� ������ lastIndexOf�� �߶󳽴�
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
			} else { //���ε��ϴ� ������ setSizeMax���� ū ���
				int overSize = (request.getContentLength() / 1000000);
				System.out.println("<script>alert('������ ũ��� 1MB���� �Դϴ�."
						+ "�ø��� ������ �뷮�� "+overSize+"MB�Դϴ�.');");
			}
		} else {
			System.out.println("���ڵ� Ÿ���� multipart/form-data�� �ƴմϴ�.");
		}
		
		return "forward:/product/getProduct.jsp";
	}

}

