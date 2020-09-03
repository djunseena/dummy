package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ClientCompanyBranchDTO;

import com.fsm.models.ClientCompanyBranch;
import com.io.iona.springboot.controllers.HibernateCRUDController;

@RestController
@RequestMapping("/client_company_branch")

public class ClientCompanyBranchController extends HibernateCRUDController<ClientCompanyBranch, ClientCompanyBranchDTO> {


}
