package com.crm.service;

import com.crm.entity.CaseComment;
import com.crm.entity.KnowledgeArticle;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SalesCustomerServiceHardeningTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void customer360CannotReadAnotherTenant() {
        ClienteRepository clients = mock(ClienteRepository.class);
        SalesOperationsService service = salesService(clients);
        TenantContext.setCurrentTenant(71L);
        when(clients.findByIdAndTenantId(12L, 71L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getCustomer360(12L));

        verify(clients, never()).findById(12L);
    }

    @Test
    void commentCannotAttachToAnotherTenantCase() {
        ServicioClienteRepository cases = mock(ServicioClienteRepository.class);
        CaseCommentRepository comments = mock(CaseCommentRepository.class);
        CustomerServiceOperationsService service = serviceOperations(cases, comments, mock(KnowledgeArticleRepository.class));
        TenantContext.setCurrentTenant(73L);
        CaseComment comment = new CaseComment();
        comment.setCaseId(15L);
        when(cases.findByIdAndTenantId(15L, 73L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.addComment(comment));

        verify(comments, never()).save(any());
    }

    @Test
    void knowledgeSearchNeverLeaksDraftsThroughSummaryMatch() {
        ServicioClienteRepository cases = mock(ServicioClienteRepository.class);
        KnowledgeArticleRepository articles = mock(KnowledgeArticleRepository.class);
        CustomerServiceOperationsService service = serviceOperations(cases, mock(CaseCommentRepository.class), articles);
        TenantContext.setCurrentTenant(73L);
        KnowledgeArticle draft = new KnowledgeArticle();
        draft.setStatus("DRAFT");
        draft.setSummary("facturacion avanzada");
        KnowledgeArticle published = new KnowledgeArticle();
        published.setStatus("PUBLISHED");
        published.setTitle("Guia de facturacion");
        when(articles.findByTenantId(73L)).thenReturn(List.of(draft, published));

        List<KnowledgeArticle> result = service.searchArticles("facturacion");

        assertEquals(List.of(published), result);
    }

    private SalesOperationsService salesService(ClienteRepository clients) {
        return new SalesOperationsService(
                mock(AccountRepository.class), mock(ContactRepository.class),
                mock(OpportunityCompetitorRepository.class), mock(CalendarEventRepository.class),
                mock(BookingPageRepository.class), mock(EmailSyncLogRepository.class),
                mock(VentaRepository.class), clients, mock(CotizacionRepository.class),
                mock(PedidoRepository.class), mock(ServicioClienteRepository.class),
                mock(ContratoRepository.class), mock(FacturaRepository.class),
                mock(SalesForecastRepository.class), mock(TerritoryRepository.class),
                mock(CommissionRepository.class), mock(MetaComercialRepository.class),
                mock(SalesSequenceRepository.class), mock(AccountTeamRepository.class),
                mock(OpportunitySplitRepository.class)
        );
    }

    private CustomerServiceOperationsService serviceOperations(
            ServicioClienteRepository cases, CaseCommentRepository comments,
            KnowledgeArticleRepository articles) {
        return new CustomerServiceOperationsService(
                cases, comments, mock(CaseAttachmentRepository.class), articles,
                mock(SLAConfiguracionRepository.class), mock(EntitlementRepository.class),
                mock(FieldServiceOrderRepository.class), mock(ServiceMilestoneRepository.class),
                mock(EncuestaSatisfaccionRepository.class), mock(LiveChatSessionRepository.class),
                mock(ChatMessageRepository.class)
        );
    }
}
