import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../../core/services/post.service';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { PostResponse, ProductResponse } from '../../models/models';

@Component({
  selector: 'app-post-composer',
  templateUrl: './post-composer.component.html',
  styleUrls: ['./post-composer.component.scss']
})
export class PostComposerComponent implements OnInit {
  @Output() posted = new EventEmitter<PostResponse>();
  @Input() compact = false;

  form!: FormGroup;
  expanded = false;
  submitting = false;
  showAdvanced = false;
  products: ProductResponse[] = [];
  selectedProducts: number[] = [];
  hashtagInput = '';

  get isBusinessOrCreator(): boolean {
    return this.authService.isBusinessOrCreator();
  }

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private productService: ProductService,
    public authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(1)]],
      postType: ['NORMAL'],
      hashtags: [''],
      ctaLabel: [''],
      ctaUrl: [''],
      scheduledTime: [''],
      pinned: [false]
    });

    if (this.isBusinessOrCreator && this.authService.userId) {
      this.productService.getUserProducts(this.authService.userId).subscribe({
        next: (products) => this.products = products,
        error: () => {}
      });
    }
  }

  expand(): void { this.expanded = true; }

  submit(): void {
    if (this.form.invalid || this.submitting) return;
    const val = this.form.value;

    const hashtags = val.hashtags
      ? val.hashtags.split(/[\s,#]+/).filter((h: string) => h.trim())
      : [];

    const payload: any = {
      content: val.content.trim(),
      postType: val.postType || 'NORMAL',
      hashtags,
      ctaLabel: val.ctaLabel || undefined,
      ctaUrl: val.ctaUrl || undefined,
      scheduledTime: val.scheduledTime || undefined,
      pinned: val.pinned || false,
      productIds: this.selectedProducts.length ? this.selectedProducts : undefined
    };

    this.submitting = true;
    this.postService.createPost(payload).subscribe({
      next: (post) => {
        this.posted.emit(post);
        this.form.reset({ postType: 'NORMAL', pinned: false });
        this.expanded = false;
        this.showAdvanced = false;
        this.selectedProducts = [];
        this.submitting = false;
        this.toastService.success('Post published!');
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Failed to create post');
        this.submitting = false;
      }
    });
  }

  toggleProduct(id: number): void {
    const idx = this.selectedProducts.indexOf(id);
    if (idx >= 0) this.selectedProducts.splice(idx, 1);
    else this.selectedProducts.push(id);
  }

  isProductSelected(id: number): boolean {
    return this.selectedProducts.includes(id);
  }

  get charCount(): number {
    return this.form.get('content')?.value?.length || 0;
  }
}
