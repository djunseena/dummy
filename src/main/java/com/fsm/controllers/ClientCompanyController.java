package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ClientCompanyDTO;
import com.fsm.models.ClientCompany;
import com.io.iona.springboot.controllers.HibernateCRUDController;

@RestController
@RequestMapping("/client_company")
public class ClientCompanyController extends HibernateCRUDController<ClientCompany, ClientCompanyDTO> {

}
