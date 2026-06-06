package com.ansh.focus.service;

import com.ansh.focus.dto.AuthResponse;
import com.ansh.focus.dto.LoginRequest;
import com.ansh.focus.dto.SignupRequest;
import com.ansh.focus.exception.DuplicateUserException;
import com.ansh.focus.exception.InvalidCredentialsException;
import com.ansh.focus.model.User;
import com.ansh.focus.repository.UserRepository;
import com.ansh.focus.security.UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new DuplicateUserException("Username is already taken");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateUserException("Email is already registered");
		}

		User user = new User(
				request.username(),
				request.email(),
				passwordEncoder.encode(request.password())
		);
		User savedUser = userRepository.save(user);
		UserPrincipal principal = new UserPrincipal(savedUser);
		String token = jwtService.generateToken(principal);

		return AuthResponse.from(savedUser, token);
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.username())
				.or(() -> userRepository.findByEmail(request.username()))
				.orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid username or password");
		}

		UserPrincipal principal = new UserPrincipal(user);
		String token = jwtService.generateToken(principal);

		return AuthResponse.from(user, token);
	}
}
