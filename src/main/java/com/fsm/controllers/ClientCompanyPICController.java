package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ClientCompanyPICDTO;
import com.fsm.models.ClientCompanyPIC;
import com.io.iona.springboot.controllers.HibernateCRUDController;

@RestController
@RequestMapping("/client_company_pic")
public class ClientCompanyPICController extends HibernateCRUDController<ClientCompanyPIC, ClientCompanyPICDTO>{

}
