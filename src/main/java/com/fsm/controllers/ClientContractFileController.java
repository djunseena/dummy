package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ClientContractFileDTO;
import com.fsm.models.ClientContractFile;
import com.io.iona.springboot.controllers.HibernateCRUDController;

@RestController
@RequestMapping("/client_contract_file")
public class ClientContractFileController extends HibernateCRUDController<ClientContractFile, ClientContractFileDTO>{

}
