-- =====================================================
-- MIGRACIÓN 9: Triggers para auditoría
-- =====================================================

-- Trigger para actualizar fecha_modificacion en nodos
CREATE TRIGGER IF NOT EXISTS tr_nodos_update 
AFTER UPDATE ON nodos
FOR EACH ROW
BEGIN
    UPDATE nodos SET fecha_modificacion = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

-- Trigger para actualizar fecha_modificacion en aristas
CREATE TRIGGER IF NOT EXISTS tr_aristas_update 
AFTER UPDATE ON aristas
FOR EACH ROW
BEGIN
    UPDATE aristas SET fecha_modificacion = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;