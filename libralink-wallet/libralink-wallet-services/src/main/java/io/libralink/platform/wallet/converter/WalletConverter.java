package io.libralink.platform.wallet.converter;

import io.libralink.platform.wallet.data.entity.Wallet;
import io.libralink.platform.wallet.dto.WalletDTO;

public final class WalletConverter {

    private WalletConverter() {}

    public static WalletDTO toDTO(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setPublicKey(wallet.getPublicKey());
        dto.setUserId(wallet.getUserId());
        dto.setCurrency(wallet.getCurrency());

        return dto;
    }
}
