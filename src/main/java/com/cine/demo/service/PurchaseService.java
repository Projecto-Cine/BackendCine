package com.cine.demo.service;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import java.util.List;

public interface PurchaseService {
    PurchaseResponseDTO create(PurchaseRequestDTO dto);
    /**
     * Confirma una compra cobrada en el mostrador. El cajero ya tiene el
     * dinero, de modo que aqui se da por pagada.
     */
    PurchaseResponseDTO confirm(Long purchaseId, PaymentMethod paymentMethod);

    /**
     * Completa una compra cuya pasarela ya confirmo el cobro.
     *
     * Existe porque hay dos formas de cobrar y ambas deben dejar la compra en
     * el mismo estado: reservar las butacas, aplicar el descuento de socio y
     * enviar la confirmacion. El webhook de Stripe solo marcaba PAID, asi que
     * una compra pagada por internet se quedaba sin butacas reservadas y sin
     * correo.
     */
    PurchaseResponseDTO completeAfterOnlinePayment(Long purchaseId);
    PurchaseResponseDTO cancel(Long purchaseId);
    PurchaseResponseDTO getById(Long id);
    List<PurchaseResponseDTO> getAll(PurchaseStatus status);
    List<PurchaseResponseDTO> getByUser(Long userId);
    List<PurchaseResponseDTO> getByScreening(Long screeningId);
}
