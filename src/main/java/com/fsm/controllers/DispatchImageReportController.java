package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchImageReportDTO;
import com.fsm.models.DispatchImageReport;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/dispatch_image_report")
public class DispatchImageReportController extends HibernateCRUDController<DispatchImageReport, DispatchImageReportDTO>{
	
}
