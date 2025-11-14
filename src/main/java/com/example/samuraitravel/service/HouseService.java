package com.example.samuraitravel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.repository.HouseRepository;


@Service     //このクラスがSpring管理のコンポーネントとして登録される。コントローラからこのクラスを自動注入（autowired）できるようになる
public class HouseService {
	private final HouseRepository houseRepository;
	
	public HouseService(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}
	
	//すべての民宿をページングされた状態で取得する
	public Page<House> findAllHouses(Pageable pageable) {
		//データベースの全レコードを取得。JpaRepositoryを継承したメソッド
		return houseRepository.findAll(pageable);
		
	}
	
	//指定されたキーワードを民宿名に含む民宿を、ページングされた状態で取得する
	public Page<House> findHousesByNameLike(String keyword, Pageable pageable){
		return houseRepository.findByNameLike("%" + keyword + "%", pageable);
	}

}
