package com.xyj.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.xyj.commons.Page;
import com.xyj.domain.Book;
import com.xyj.domain.Category;
import com.xyj.service.BusinessService;
import com.xyj.service.impl.BusinessServiceImpl;
import com.xyj.util.FileBeanUtil;

public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	BusinessService bs = new BusinessServiceImpl();
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		if("addCategory".equals(op)){
			addCategoty(request,response);
		}if("deleteCategory".equals(op)){
			deleteCategory(request,response);
		}if("showAllCategory".equals(op)){
			showAllCategory(request,response);
		}if("showAddBook".equals(op)){
			showAddBook(request,response);
		}if("addBook".equals(op)){
			try {
				addBook(request,response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}if("showAllBook".equals(op)){
			showAllBook(request,response);
		}if("deleteBook".equals(op)){
			deleteBook(request,response);
		}
		
	}

	private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String category_id = request.getParameter("categoryId");
		bs.deleteCategory(category_id);
		request.setAttribute("msg","ɾ���ɹ�");
		request.getRequestDispatcher("/manager/message.jsp").forward(request, response);
	}

	private void deleteBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bookId= request.getParameter("bookId");
		bs.deleteBook(bookId);
		request.setAttribute("msg","ɾ���ɹ�");
		request.getRequestDispatcher("/manager/message.jsp").forward(request, response);
	}

	private void showAllBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pageNum = request.getParameter("pageNum");
		Page page = bs.findPageBook(pageNum);
		page.setUrl("/servlet/ControlServlet?op=showAllBook");
		request.setAttribute("page", page);
		request.getRequestDispatcher("/manager/listBook.jsp").forward(request, response);
	}

	private void addBook(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//��װ�ϴ�����������
		Boolean isMulti = ServletFileUpload.isMultipartContent(request);
		if(!isMulti){
			request.setAttribute("msg","���ǣ���ı���������");
			request.getRequestDispatcher("/manager/message.jsp").forward(request, response);
		}
		DiskFileItemFactory dfif = new DiskFileItemFactory();
		ServletFileUpload sfu = new ServletFileUpload(dfif);
		List<FileItem> items = sfu.parseRequest(request);
		Book book = new Book();
		for(FileItem item: items){
			if(item.isFormField()){
				String filedName = item.getFieldName();
				String Filedvalue = item.getString(request.getCharacterEncoding());
				BeanUtils.setProperty(book, filedName, Filedvalue);
			}else{
				String fileName = item.getName();
				if(fileName!=null&&!fileName.trim().equals("")){
					fileName=UUID.randomUUID().toString()+"."+FilenameUtils.getExtension(fileName);
					String storeDirectory = getServletContext().getRealPath("/images");
					System.out.println(storeDirectory);
					String path = makeDirs(storeDirectory,fileName);
							
					book.setPath(path);
					book.setFilename(fileName);
					item.write(new File(storeDirectory+path+"/"+fileName));
					
				}
			}
		}
		//�����鼮��Ϣ�����ݿ���
		bs.addBook(book);
		request.setAttribute("msg","�鼮����ɹ���");
		request.getRequestDispatcher("/manager/message.jsp").forward(request, response);
	}

	private void showAddBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<Category> lists = bs.findAllCategory();
		request.setAttribute("lists", lists);
		request.getRequestDispatcher("/manager/addBook.jsp").forward(request, response);
		
	}

	private void showAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Category> lists = bs.findAllCategory();
		
		request.setAttribute("lists", lists);
		request.getRequestDispatcher("/manager/listCategory.jsp").forward(request, response);
		
	}

	private void addCategoty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Category c = FileBeanUtil.fillBean(request, Category.class);
		bs.addCategory(c);
		
		request.setAttribute("msg", "����ɹ�!");
		request.getRequestDispatcher("/manager/message.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private String makeDirs(String storeDirectory, String fileName) {
		int hashCode = fileName.hashCode();
		int dir1 = hashCode&0xf;
		int dir2 = (hashCode&0xf0)>>4;
		String newPath = "/"+dir1+"/"+dir2;
		File file = new File(storeDirectory,newPath);
		if(!file.exists()){
			file.mkdirs();
		}
		return newPath;
	}

}
