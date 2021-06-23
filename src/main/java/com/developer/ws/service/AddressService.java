package com.developer.ws.service;

import java.util.List;

import com.developer.ws.shared.dto.AddressDto;

public interface AddressService {
	List<AddressDto> getAddresses(String userId);

	AddressDto getAddress(String addressId);
}
