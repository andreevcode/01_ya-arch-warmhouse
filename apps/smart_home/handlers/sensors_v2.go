package handlers

import (
	"context"
	"net/http"

	"github.com/gin-gonic/gin"
	"smarthome/db"
)

// SensorHandlerV2 handles sensor-related requests
type SensorHandlerV2 struct {
	DB *db.DB
}

// NewSensorHandler creates a new SensorHandler
func NewSensorHandlerV2(db *db.DB) *SensorHandlerV2 {
	return &SensorHandlerV2{
		DB: db,
	}
}

// RegisterRoutes registers the sensor routes
func (h *SensorHandlerV2) RegisterRoutes(router *gin.RouterGroup) {
	sensors := router.Group("/sensors")
	{
		sensors.GET("", h.GetSensors)
	}
}

// GetSensors handles GET /api/v2/sensors
func (h *SensorHandlerV2) GetSensors(c *gin.Context) {
	sensors, err := h.DB.GetSensorsV2(context.Background())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, sensors)
}
