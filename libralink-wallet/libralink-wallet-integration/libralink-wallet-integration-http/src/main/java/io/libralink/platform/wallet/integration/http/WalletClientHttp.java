package io.libralink.platform.wallet.integration.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.common.IntegrationException;
import io.libralink.platform.wallet.integration.api.WalletClient;
import io.libralink.platform.wallet.integration.dto.IntegrationDepositApprovalDTO;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WalletClientHttp implements WalletClient {

    @Value(value = "${external.wallet.address}")
    private String walletBaseAddress;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public IntegrationECheckDTO register(IntegrationECheckDTO dto, String systemToken) throws ApplicationException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(systemToken);

        String body;
        try {
            body = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IntegrationException(e.getMessage());
        }

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<IntegrationECheckDTO> response = restTemplate.exchange(String.format("%s/payment/create-account-key-pair",
                walletBaseAddress), HttpMethod.POST, request, IntegrationECheckDTO.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IntegrationException("E-Check Issue unsuccessful");
        }

        return response.getBody();
    }

    @Override
    public void register(IntegrationDepositApprovalDTO dto) throws ApplicationException {

    }
}
