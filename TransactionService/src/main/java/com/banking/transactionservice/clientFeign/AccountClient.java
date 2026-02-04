package com.banking.transactionservice.clientFeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

}
