package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.repository.HouseRepository;


@Service     //このクラスがSpring管理のコンポーネントとして登録される。コントローラからこのクラスを自動注入（autowired）できるようになる
public class HouseService {
	private final HouseRepository houseRepository;
	
	public HouseService(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	
	//すべての民宿を取得する
	public List<House> findAllHouses() {
		//データベースの全レコードを取得。JpaRepositoryを継承したメソッド
		return houseRepository.findAll();
		
	}

}
