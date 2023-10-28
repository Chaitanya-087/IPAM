package com.ipam.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.UserDTO;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.SubnetRepository;
import com.ipam.api.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IPRangeRepository ipRangeRepository;

    @Autowired
    private IPAddressRepository ipAddressRepository;

    @Autowired
    private SubnetRepository subnetRepository;

    public PageResponse<User> getAllUsers(int page, int size) {
        return convertToPageResponse(userRepository.findByRole("USER",PageRequest.of(page,size)));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());  
        userDTO.setIpAddressesCount(ipAddressRepository.countByUser(user));
        userDTO.setIpRangesCount(ipRangeRepository.countByUser(user));
        userDTO.setSubnetsCount(subnetRepository.countByUser(user));
        return userDTO;
    }


   private PageResponse<User> convertToPageResponse(Page<User> page) {
    PageResponse<User> response = new PageResponse<>();
    response.setTotalPages(page.getTotalPages());
    response.setCurrentPage(page.getNumber());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());
    response.setData(page.getContent().stream().map(this::convertToDTO).toList());
    response.setTotalElements(page.getTotalElements());
    response.setMaxPageSize(page.getSize());
    response.setCurrentPageSize(page.getNumberOfElements());
    return response;
  }
    
}
