package com.open.capacity.server.dto;

import java.util.List;
import java.util.Set;

import com.open.capacity.server.model.Client;

import lombok.Data;

@Data
public class ClientDto extends Client {

    private static final long serialVersionUID = 1475637288060027265L;

    private List<Long> permissionIds;

    private Set<Long> serviceIds;

}
