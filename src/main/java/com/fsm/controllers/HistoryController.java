package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.HistoryDTO;
import com.fsm.models.History;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/history")
public class HistoryController extends HibernateCRUDController<History, HistoryDTO>{
	
}
