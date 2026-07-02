export interface Equipment {
  id: number;
  name: string;
  image_path: string;
  sport_type: string;
  price: number;
  stock_quantity: number;
  description: string;
}

export interface CartItemRequest {
  equipment_id: number;
  quantity: number;
}

export interface OrderRequest {
  items: CartItemRequest[];
}

export interface OrderResponse {
  id: number;
  order_date: string;
  total_price: number;
  status: string; // ORDERED | DELIVERED | CANCELLED
  items_summary: string;
  total_original_price: number;
}

export interface Order {
  id: number;
  user_id: number;
  order_date: string;
  total_price: number;
  status: string;
}

export interface OrderItem {
  id: number;
  order_id: number;
  equipment_id: number;
  quantity: number;
  unit_price: number;
  discount_percent: number;
  final_price: number;
}