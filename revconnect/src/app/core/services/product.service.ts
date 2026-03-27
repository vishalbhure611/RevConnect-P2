import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductRequest, ProductResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private base = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // POST /api/products/{ownerId}
  createProduct(ownerId: number, product: ProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(`${this.base}/products/${ownerId}`, product);
  }

  updateProduct(productId: number, product: ProductRequest): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(`${this.base}/products/${productId}`, product);
  }

  deleteProduct(productId: number): Observable<any> {
    return this.http.delete(`${this.base}/products/${productId}`);
  }

  // GET /api/products/owner/{ownerId}
  getUserProducts(ownerId: number): Observable<ProductResponse[]> {
    return this.http.get<ProductResponse[]>(`${this.base}/products/owner/${ownerId}`);
  }
}
