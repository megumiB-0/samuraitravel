package com.example.samuraitravel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
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
	
	//指定したidを持つ民宿を取得する
	public Optional<House> findHouseById(Integer id) {
		return houseRepository.findById(id);
	}
	
	// 民宿のレコード数を取得する
	public long countHouses() {
		return houseRepository.count();
	}
	
	// idの最も大きい民宿を取得する
	public House findFirstHouseByOrderByIdDesc(){
		return houseRepository.findFirstByOrderByIdDesc();
	}
	
	@Transactional
	public void createHouse(HouseRegisterForm houseRegisterForm) {
		//新しいエンティティを作成
		House house = new House();
		// フォームから送られたファイルを受け取る
		MultipartFile imageFile = houseRegisterForm.getImageFile();
		// ファイルがある場合のみ画像処理を行う
		if(!imageFile.isEmpty()) {
			// ブラウザが送ってきた元のファイル名（拡張子含む）	
			String imageName = imageFile.getOriginalFilename();
			// 実際に保存するファイル名をUUIDベースで生成
			String hashedImageName = generateNewFileName(imageName);
			// 保存先パスをハードコードで生成している
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			// 実際にファイルをコピーIOExceptionをcatchしている点に注意
			copyImageFile(imageFile, filePath);
			// エンティティに保存するファイル名を設定
			house.setImageName(hashedImageName);
		}
		// 各フィールドをフォームからコピー
		house.setName(houseRegisterForm.getName());
		house.setDescription(houseRegisterForm.getDescription());
		house.setPrice(houseRegisterForm.getPrice());
		house.setCapacity(houseRegisterForm.getCapacity());
		house.setPostalCode(houseRegisterForm.getPostalCode());
		house.setAddress(houseRegisterForm.getAddress());
		house.setPhoneNumber(houseRegisterForm.getPhoneNumber());
		// DBにエンティティを保存
		houseRepository.save(house);
	}
	// 更新機能
	@Transactional
	public void updateHouse(HouseEditForm houseEditForm,House house) {
		MultipartFile imageFile = houseEditForm.getImageFile();
		
		if(!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath =Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			house.setImageName(hashedImageName);
		}
		house.setName(houseEditForm.getName());
		house.setDescription(houseEditForm.getDescription());
		house.setPrice(houseEditForm.getPrice());
		house.setCapacity(houseEditForm.getCapacity());
		house.setPostalCode(houseEditForm.getPostalCode());
		house.setAddress(houseEditForm.getAddress());
		house.setPhoneNumber(houseEditForm.getPhoneNumber());
		
		houseRepository.save(house);		
	}
	
	// UUIDを使って生成したファイル名を返す（元ファイルの拡張子は残したまま拡張子より前の部分をUUIDに置き換え）
	public String generateNewFileName(String fileName) {
		// ファイル名をドット区切りで分割（ex. house.jpg="house" + "jpg"）
		String[] fileNames = fileName.split("\\.");
		// 拡張子以外の部分の処理をループ（配列の０〜最後の1つ手前までをUUIDに置き換え）
		for (int i = 0;i < fileNames.length -1; i++) {
			//UUIDに置き換え
			fileNames[i] = UUID.randomUUID().toString();
		}
		// .で再び連結してファイル名に戻す
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;	
	}
	
	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			//　.getInputStream()  MultipartFileが持つメソッドでアップロードされたデータを読み込むためのInputStreamを返すメソッド
			Files.copy(imageFile.getInputStream(), filePath);
			}catch(IOException e){
				e.printStackTrace();
		}
	}
	
	
}

