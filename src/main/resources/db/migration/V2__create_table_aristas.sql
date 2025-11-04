CREATE TABLE IF NOT EXISTS arista (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    origen_id      INTEGER NOT NULL REFERENCES nodo(id) ON DELETE CASCADE,
    destino_id     INTEGER NOT NULL REFERENCES nodo(id) ON DELETE CASCADE,
    distancia      REAL    NOT NULL CHECK (distancia > 0),
    
    UNIQUE (origen_id, destino_id),
    CHECK (origen_id != destino_id)
);