package com.haizhi.empower;

import cn.bdp.joif.autoconf.enable.EnableJoifUc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJoifUc
public class EmpowerApplication {
   //启动
	public static void main(String[] args) {
		SpringApplication.run(EmpowerApplication.class, args);
	}

}
