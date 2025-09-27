from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Boolean
from sqlalchemy.dialects.postgresql import ARRAY
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship

from src.app.config import BASE_URL

Base = declarative_base()


class Plant(Base):
    __tablename__ = 'plants'

    id = Column(Integer, primary_key=True)
    name = Column(String, nullable=False)
    family = Column(String, nullable=False)
    genus = Column(String, nullable=False)
    growing_area = Column(String, nullable=False)
    height = Column(String, nullable=True)
    class_type = Column(String, nullable=True)
    has_fruits = Column(Boolean, default=False)

    @property
    def data(self):
        return {
            'id': self.id,
            'name': self.name,
            'family': self.family,
            'genus': self.genus,
            'growing_area': self.growing_area,
            'height': self.height,
            'class_type': self.class_type,
            'has_fruits': self.has_fruits,
        }


class Order(Base):
    __tablename__ = 'orders'

    id = Column(Integer, primary_key=True)
    image_path = Column(String, nullable=False)
    title = Column(String, nullable=False)
    created_at = Column(DateTime, nullable=False)
    season = Column(String, nullable=False, default='вегетационный')

    detection_results = relationship("DetectionResult", back_populates="order")

    @property
    def data(self):
        return {
            'id': self.id,
            'season': self.season,
            'image_path': f'{BASE_URL}api/v1/images/{self.image_path}/',
            'title': self.title,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'results': [detection_result.data for detection_result in self.detection_results],
            'statement': f'{BASE_URL}api/v1/statement/{self.id}/'
        }


class DetectionResult(Base):
    __tablename__ = 'detection_results'

    id = Column(Integer, primary_key=True)
    order_id = Column(Integer, ForeignKey('orders.id'), nullable=False)
    label = Column(String, nullable=False)
    season = Column(String, nullable=False, default='вегетационный')
    name_plant = Column(String, nullable=False, default='Неизвестный вид')
    bbox_abs = Column(ARRAY(Float), nullable=False)
    bbox_norm = Column(ARRAY(Float), nullable=False)
    dry_branches_percentage = Column(Float, default=0.0)
    status = Column(Integer, default=1)

    order = relationship("Order", back_populates="detection_results")

    @property
    def data(self):
        return {
            'id': self.id,
            'label': self.label,
            'name_plant': self.name_plant,
            'season': self.season,
            'bbox_abs': self.bbox_abs,
            'bbox_norm': self.bbox_norm,
            'dry_branches_percentage': self.dry_branches_percentage,
        }
