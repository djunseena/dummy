package com.fsm.repositories.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.models.Users;
import com.fsm.repositories.UsersRepository;
import com.fsm.utility.HashUtil.SHA_256;

import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("api")
public class ChangePasswordMobileRepositoryController {

	@Autowired
	private UsersRepository usersRepository;

	@PostMapping("/changePasswordUserWorker/{id}")
	public HashMap<String, Object> changePassword(@PathVariable(value = "id") Long id,
			@RequestBody ChangePassword changePassword) {

		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		String status = "";
		Users users = usersRepository.findById(id).get();

		if (!oldPasswordValidation(users, SHA_256.digestAsHex(changePassword.getOldPassword()))) {
			message = "Password lama yang anda masukkan tidak sesuai";
			status = "Gagal!";
		} else if (!newPasswordValidation(SHA_256.digestAsHex(changePassword.getOldPassword()),
				SHA_256.digestAsHex(changePassword.getNewPassword()))) {
			message = "Password baru tidak boleh sama dengan password lama";
			status = "Gagal!";
		} else if (!confirmPasswordValidation(SHA_256.digestAsHex(changePassword.getNewPassword()),
				SHA_256.digestAsHex(changePassword.getConfirmPassword()))) {
			message = "Password yang anda masukkan tidak sesuai";
			status = "Gagal!";
		} else {
			users.setUserPassword(SHA_256.digestAsHex(changePassword.getNewPassword()));
			users.setLastModifiedOn(dateNow);
			users.setCreatedBy(users.getUserId());
			users.setLastModifiedBy(users.getUserId());

			usersRepository.save(users);
			message = "Ganti Password Berhasil";
			status = "Sukses!";
		}

		showHashMap.put("Status", status);
		showHashMap.put("Message", message);

		return showHashMap;
	}

	@Data
	@NoArgsConstructor
	static class ChangePassword {
		private String oldPassword;
		private String newPassword;
		private String confirmPassword;
	}

	// Validasi untuk mengecek bahwa password lama sesuai
	public boolean oldPasswordValidation(Users users, String oldPassword) {
		boolean isMatch = false;
		if (SHA_256.digestAsHex(oldPassword).equalsIgnoreCase(SHA_256.digestAsHex(users.getUserPassword()))) {
			isMatch = true;
		}
		return isMatch;
	}

	// Validasi untuk mengecek bahwa password baru yang diinputkan tidak sama dengan
	// password lama
	public boolean newPasswordValidation(String oldPassword, String newPassword) {
		boolean isValid = false;
		if (!SHA_256.digestAsHex(oldPassword).equalsIgnoreCase(SHA_256.digestAsHex(newPassword))) {
			isValid = true;
		}
		return isValid;
	}

	// Validasi untuk mengecek bahwa confirm password sama dengan password baru yang
	// diinputkan
	public boolean confirmPasswordValidation(String newPassword, String confirmPassword) {
		boolean isValid = false;
		if (SHA_256.digestAsHex(newPassword).equalsIgnoreCase(SHA_256.digestAsHex(confirmPassword))) {
			isValid = true;
		}
		return isValid;
	}
}