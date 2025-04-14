package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Wallet;
import io.libralink.platform.wallet.dto.WalletDTO;

public final class WalletConverter {

    private WalletConverter() {}

    public static WalletDTO toDTO(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUserId());
        dto.setCurrency(wallet.getCurrency());
        dto.setPublicKey(wallet.getPublicKey());
        dto.setAddress(wallet.getAddress());
        dto.setAlgorithm(wallet.getAlgorithm());

        return dto;
    }
}
