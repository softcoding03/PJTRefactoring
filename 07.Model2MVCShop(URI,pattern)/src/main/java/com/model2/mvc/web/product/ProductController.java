package com.model2.mvc.web.product;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.user.UserService;


//==> ��ǰ���� Controller
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	//setter Method ���� ����
		
	public ProductController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml ���� �Ұ�
	//==> �Ʒ��� �ΰ��� �ּ��� Ǯ�� �ǹ̸� Ȯ�� �Ұ�
	//@Value("#{commonProperties['pageUnit']}")
	@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	//@Value("#{commonProperties['pageSize']}")
	@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	//@RequestMapping("/addUserView.do")
	//public String addProductView() throws Exception {
	
	@RequestMapping(value="addProduct", method=RequestMethod.GET)
	public String addProduct() throws Exception {

		System.out.println("/product/addProduct : GET");
		
		return "forward:/product/addProductView.jsp";
	}
	
	@RequestMapping(value="addProduct", method=RequestMethod.POST)
	public String addProduct(@ModelAttribute("product") Product product,
							@RequestParam("file") MultipartFile[] uploadFile,
							Model model) throws Exception {

		String path = 
		"C:\\Users\\user\\git\\07.Model2MVC(URI,pattern)\\07.Model2MVCShop(URI,pattern)\\src\\main\\webapp\\images\\uploadFiles";
		System.out.println("uploadFile�Ѿ���°� ������ ? :" + uploadFile);
		
		for(MultipartFile file : uploadFile){
			
			//���ϸ� ��������
            String originalName = file.getOriginalFilename();
    			System.out.println("originalName�� ??? : "+originalName);
    			
		    			//���ʿ��� �ڵ�? �������?
		    			//String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
		    			//	System.out.println("fileName�� ??? : "+fileName);
    		
    		//����ũ�� ���� ������ -> ���� �� ���ε� �� ���
            String uuid = UUID.randomUUID().toString();
           
            //������ ���� ��� ����: �Ϲ� ��� + ����ũ�� ���� + ���ϸ�
            String savefileName = path + File.separator + uuid + "_" + originalName;
            			System.out.println("savfileName�� ??? : "+savefileName);
            
            //������ ��θ� ���� �� ����
            Path savePath = Paths.get(savefileName);
						System.out.println("savePath�� ??? : "+savePath);
            
			//product ������ ��ü�� fileName �������ֱ�
            product.setFileName(originalName);
            
            try {
                file.transferTo(savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		
		System.out.println("/product/addProduct : POST");
		//Business Logic
		productService.addProduct(product);
		model.addAttribute("product", product);
		
		return "forward:/product/addProduct.jsp";
	}
	/*
	@RequestMapping(value="addProduct", method=RequestMethod.POST)
	public String addProduct(@ModelAttribute("product") Product product,
							@RequestParam("file") MultipartFile file,
							Model model) throws Exception {
//���ϳ����� string ���·� ��.
		String path = 
		"C:\\Users\\user\\git\\07.Model2MVC(URI,pattern)\\07.Model2MVCShop(URI,pattern)\\src\\main\\webapp\\images\\uploadFiles";
		System.out.println("��������");
		 
		if (!file.getOriginalFilename().isEmpty()) {
		      file.transferTo(new File(path, file.getOriginalFilename()));
		      model.addAttribute("msg", "File uploaded successfully.");
		   } else {
		      model.addAttribute("msg", "Please select a valid mediaFile..");
		  }
		 
		product.setFileName(file.getOriginalFilename());
		System.out.println("/product/addProduct : POST");
		//Business Logic
		productService.addProduct(product);
		model.addAttribute("product", product);
		
		return "forward:/product/addProduct.jsp";
	}*/
	
//	@RequestMapping("/getProduct.do")
	@RequestMapping(value="getProduct", method=RequestMethod.GET)
	public String getProduct(@RequestParam("prodNo") int prodNo, Model model ) throws Exception {
		
		System.out.println("/product/getProduct : GET");
		System.out.println("prodNo int�� �� ����Ǿ����� ?  --> "+prodNo);
		
		//Business Logic
		Product product = productService.getProduct(prodNo);
		
		// Model �� View ����
		model.addAttribute("product", product);
		
		return "forward:/product/getProduct.jsp";
	}
	
//	@RequestMapping("/listProduct.do")
	@RequestMapping(value="listProduct")
	public String listProduct( @ModelAttribute("search") Search search,
							@RequestParam("menu") String menu,
							Model model , HttpServletRequest request) throws Exception{
		
		System.out.println("/product/listProduct : GET / POST");
		System.out.println("menu �� �Ѿ� �Գ��� ?? --->"+menu);
		
		if(search.getCurrentPage()==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic ����
		Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model �� View ����
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		System.out.println("resultpage �� ���õǾ����� ??? --->"+resultPage);
		System.out.println("menu �� �Ѿ� �Գ��� 222?? --->"+menu);
		System.out.println("list �� �Ѿ� �Գ��� ?? --->"+map.get("list"));

		if (menu.equals("manage")) {
			return "forward:/product/listProductManage.jsp";
		}else {
			return "forward:/product/listProductSearch.jsp";
		}
	}
		
//	@RequestMapping("/updateProductView.do")
	@RequestMapping(value="updateProduct", method=RequestMethod.GET)
	public String updateProduct(@RequestParam("prodNo") int prodNo , Model model ) throws Exception{

		System.out.println("/product/updateProduct : GET");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model �� View ����
		model.addAttribute("product", product);
		
		return "forward:/product/updateProductView.jsp";
	}

	
//	@RequestMapping("/updateProduct.do")
	@RequestMapping(value="updateProduct", method=RequestMethod.POST)
	public String updateProduct(@ModelAttribute("product") Product product,
								Model model , HttpSession session) throws Exception{
		
		System.out.println("product ??? -> "+ product);
		System.out.println("/product/updateProduct : POST");
		//Business Logic
		productService.updateProduct(product);
		
		Product product2 = productService.getProduct(product.getProdNo());
		model.addAttribute("product", product2);
		return "forward:/product/updateProduct.jsp";
	}
}