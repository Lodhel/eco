from sqlalchemy import Column, Integer, String, JSON, DateTime, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship

Base = declarative_base()


class Order(Base):
    __tablename__ = 'orders'

    id = Column(Integer, primary_key=True)
    image_path = Column(String, nullable=False)
    title = Column(String, nullable=False)
    created_at = Column(DateTime, nullable=False)

    detection_results = relationship("DetectionResult", back_populates="order")

    @property
    def data(self):
        return {
            'id': self.id,
            'image_path': self.image_path,
            'title': self.title,
            'created_at': self.created_at,
            'results': [detection_result.data for detection_result in self.detection_results],
        }


class DetectionResult(Base):
    __tablename__ = 'detection_results'

    id = Column(Integer, primary_key=True)
    order_id = Column(Integer, ForeignKey('orders.id'), nullable=False)

    label = Column(String, nullable=False)
    data = Column(JSON, nullable=False)

    order = relationship("Order", back_populates="detection_results")
