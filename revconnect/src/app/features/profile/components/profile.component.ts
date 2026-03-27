import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { ConnectionService } from '../../../core/services/connection.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ProductService } from '../../../core/services/product.service';
import { UserResponse, PostResponse, ProfileRequest, UpdateAccountRequest, ProductRequest, ProductResponse } from '../../../shared/models/models';

type ProfileTab = 'posts' | 'followers' | 'following';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit, OnDestroy {
  private static readonly MAX_PROFILE_IMAGE_BYTES = 1024 * 1024;

  user: UserResponse | null = null;
  posts: PostResponse[] = [];
  followers: UserResponse[] = [];
  following: UserResponse[] = [];
  loading = true;
  activeTab: ProfileTab = 'posts';
  isOwnProfile = false;
  showEditModal = false;
  editForm!: FormGroup;
  editLoading = false;
  connectionStatus: string | null = null;
  isFollowing = false;
  followersLoaded = false;
  followingLoaded = false;
  profileImagePreview: string | null = null;
  products: ProductResponse[] = [];
  productsLoading = false;
  showProductModal = false;
  productForm!: FormGroup;
  productLoading = false;
  editingProduct: ProductResponse | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    private connectionService: ConnectionService,
    public authService: AuthService,
    private toastService: ToastService,
    private productService: ProductService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.reset();
      this.loadProfile(+params['id']);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  reset(): void {
    this.user = null; this.posts = []; this.followers = []; this.following = [];
    this.loading = true; this.followersLoaded = false;
    this.followingLoaded = false; this.activeTab = 'posts';
    this.products = []; this.productsLoading = false; this.showProductModal = false;
    this.editingProduct = null;
  }

  loadProfile(userId: number): void {
    this.isOwnProfile = userId === this.authService.userId;
    this.userService.getProfile(userId).pipe(
      catchError(() => of(null)),
      takeUntil(this.destroy$)
    ).subscribe(profile => {
      if (profile) {
        this.user = {
          id: profile.userId,
          username: profile.username,
          email: this.isOwnProfile ? (this.authService.currentUser?.email || '') : '',
          role: profile.role as any,
          profile,
          followersCount: profile.followersCount,
          followingCount: profile.followingCount,
          connectionsCount: profile.connectionsCount,
          isFollowing: profile.isFollowing,
          isConnected: profile.isConnected,
          connectionStatus: profile.connectionStatus || undefined
        };

        this.isFollowing = !!profile.isFollowing;
        this.connectionStatus = profile.connectionStatus || null;
        this.loadPosts(userId);
        if (profile.role === 'BUSINESS' || profile.role === 'CREATOR') this.loadProducts(userId);
        if (this.isOwnProfile) this.initEditForm(this.user);
      } else {
        this.user = null;
      }
      this.loading = false;
    });
  }

  loadPosts(userId: number): void {
    this.postService.getUserPosts(userId).pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(p => { this.posts = Array.isArray(p) ? p : []; });
  }

  loadFollowers(): void {
    if (!this.user || this.followersLoaded) return;
    this.userService.getFollowers(this.user.id).pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(u => { this.followers = Array.isArray(u) ? u : []; this.followersLoaded = true; });
  }

  loadFollowing(): void {
    if (!this.user || this.followingLoaded) return;
    this.userService.getFollowing(this.user.id).pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(u => { this.following = Array.isArray(u) ? u : []; this.followingLoaded = true; });
  }

  setTab(tab: ProfileTab): void {
    this.activeTab = tab;
    if (tab === 'followers') this.loadFollowers();
    if (tab === 'following') this.loadFollowing();
  }

  initEditForm(user: UserResponse): void {
    const p = user.profile;
    this.profileImagePreview = p?.profilePictureUrl || null;
    this.editForm = this.fb.group({
      username: [user.username || ''],
      email: [user.email || ''],
      password: [''],
      fullName: [p?.fullName || ''],
      bio: [p?.bio || ''],
      location: [p?.location || ''],
      website: [p?.website || ''],
      profilePictureUrl: [p?.profilePictureUrl || ''],
      privacy: [p?.privacy || 'PUBLIC'],
      category: [p?.category || ''],
      contactEmail: [p?.contactEmail || ''],
      contactPhone: [p?.contactPhone || ''],
      businessAddress: [p?.businessAddress || ''],
      businessHours: [p?.businessHours || ''],
      externalLinks: [this.externalLinksText(p?.externalLinks || '')]
    });

    this.productForm = this.fb.group({
      name: [''],
      description: [''],
      url: [''],
      price: ['']
    });
  }

  loadProducts(userId: number): void {
    this.productsLoading = true;
    this.productService.getUserProducts(userId).pipe(
      catchError(() => of([])),
      takeUntil(this.destroy$)
    ).subscribe(products => {
      this.products = Array.isArray(products) ? products : [];
      this.productsLoading = false;
    });
  }

  onProfileImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const file = input?.files?.[0];

    if (!file) {
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.toastService.error('Please select an image file');
      input.value = '';
      return;
    }

    if (file.size > ProfileComponent.MAX_PROFILE_IMAGE_BYTES) {
      this.toastService.error('Please choose an image smaller than 1 MB');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const result = typeof reader.result === 'string' ? reader.result : '';
      this.profileImagePreview = result || null;
      this.editForm.patchValue({ profilePictureUrl: result });
    };
    reader.onerror = () => {
      this.toastService.error('Failed to read the selected image');
    };
    reader.readAsDataURL(file);
  }

  removeSelectedProfileImage(): void {
    this.profileImagePreview = null;
    this.editForm.patchValue({ profilePictureUrl: '' });
  }

  saveProfile(): void {
    if (!this.user || !this.editForm) return;
    this.editLoading = true;
    const formValue = this.editForm.value;
    const accountData: UpdateAccountRequest = {
      username: formValue.username?.trim() || undefined,
      email: formValue.email?.trim() || undefined,
      password: formValue.password?.trim() || undefined
    };
    const profileData: ProfileRequest = {
      fullName: formValue.fullName,
      bio: formValue.bio,
      location: formValue.location,
      website: formValue.website,
      profilePictureUrl: formValue.profilePictureUrl,
      privacy: formValue.privacy,
      category: formValue.category,
      contactEmail: formValue.contactEmail,
      contactPhone: formValue.contactPhone,
      businessAddress: formValue.businessAddress,
      businessHours: formValue.businessHours,
      externalLinks: this.serializeExternalLinks(formValue.externalLinks)
    };
    const accountChanged =
      accountData.username !== this.user.username ||
      accountData.email !== this.user.email ||
      !!accountData.password;

    const accountRequest$ = accountChanged
      ? this.userService.updateAccount(this.user.id, accountData)
      : of(this.user);

    accountRequest$.pipe(
      switchMap((updatedUser) =>
        this.userService.updateProfile(this.user!.id, profileData).pipe(
          switchMap((profile) => of({ updatedUser, profile }))
        )
      ),
      catchError(() => of(null))
    ).subscribe(result => {
      if (result) {
        this.user = {
          ...this.user!,
          ...result.updatedUser,
          profile: result.profile,
          followersCount: result.profile.followersCount,
          followingCount: result.profile.followingCount
        };
        this.authService.updateSession({
          username: this.user.username,
          email: this.user.email,
          profilePictureUrl: result.profile.profilePictureUrl || ''
        });
        this.toastService.success(accountChanged
          ? 'Profile updated. Please log in again if you changed your username or password.'
          : 'Profile updated!');
      } else {
        this.toastService.error('Failed to update profile');
      }
      this.showEditModal = false;
      this.profileImagePreview = this.user?.profile?.profilePictureUrl || null;
      this.editLoading = false;
    });
  }

  follow(): void {
    if (!this.user) return;
    this.connectionService.follow(this.user.id).subscribe({
      next: () => {
        this.isFollowing = true;
        if (this.user!.followersCount !== undefined) this.user = { ...this.user!, followersCount: (this.user!.followersCount || 0) + 1, isFollowing: true };
        if (this.followersLoaded && this.authService.currentUser) {
          this.followers = [{
            id: this.authService.userId!,
            username: this.authService.currentUser.username,
            email: this.authService.currentUser.email,
            role: (this.authService.userRole as any) || 'PERSONAL'
          }, ...this.followers.filter(f => f.id !== this.authService.userId)];
        }
        this.toastService.success('Following!');
      },
      error: (err) => this.toastService.error(err?.error?.message || 'Failed to follow')
    });
  }

  unfollow(): void {
    if (!this.user) return;
    this.connectionService.unfollow(this.user.id).subscribe({
      next: () => {
        this.isFollowing = false;
        if (this.user!.followersCount) this.user = { ...this.user!, followersCount: this.user!.followersCount! - 1, isFollowing: false };
        if (this.followersLoaded) {
          this.followers = this.followers.filter(f => f.id !== this.authService.userId);
        }
        this.toastService.info('Unfollowed');
      },
      error: () => this.toastService.error('Failed to unfollow')
    });
  }

  sendConnectionRequest(): void {
    if (!this.user) return;
    this.connectionService.sendRequest(this.authService.userId!, this.user.id).subscribe({
      next: () => {
        this.connectionStatus = 'PENDING';
        this.user = { ...this.user!, connectionStatus: 'PENDING' };
        this.toastService.success('Connection request sent!');
      },
      error: (err) => this.toastService.error(err?.error?.message || 'Failed')
    });
  }

  onPostDeleted(id: number): void {
    this.posts = this.posts.filter(p => p.id !== id);
  }

  onPostCreated(post: PostResponse): void {
    this.posts = [post, ...this.posts];
  }

  openProductModal(product?: ProductResponse): void {
    if (!this.isOwnProfile || !this.user || !this.isBusinessOrCreator) return;

    this.editingProduct = product || null;
    this.showProductModal = true;
    this.productForm.patchValue({
      name: product?.name || '',
      description: product?.description || '',
      url: product?.url || '',
      price: product?.price ?? ''
    });
  }

  closeProductModal(): void {
    this.showProductModal = false;
    this.productLoading = false;
    this.editingProduct = null;
    this.productForm.reset({
      name: '',
      description: '',
      url: '',
      price: ''
    });
  }

  saveProduct(): void {
    if (!this.user || !this.productForm || this.productLoading) return;

    const value = this.productForm.value;
    const payload: ProductRequest = {
      name: value.name?.trim(),
      description: value.description?.trim() || undefined,
      url: value.url?.trim() || undefined,
      price: value.price === '' || value.price === null ? undefined : Number(value.price)
    };

    if (!payload.name) {
      this.toastService.error('Product name is required');
      return;
    }

    this.productLoading = true;

    const request$ = this.editingProduct
      ? this.productService.updateProduct(this.editingProduct.id, payload)
      : this.productService.createProduct(this.user.id, payload);

    request$.pipe(
      catchError(() => of(null)),
      takeUntil(this.destroy$)
    ).subscribe(result => {
      if (result) {
        this.products = this.editingProduct
          ? this.products.map(product => product.id === result.id ? result : product)
          : [result, ...this.products];
        this.toastService.success(this.editingProduct ? 'Product updated!' : 'Product added!');
        this.closeProductModal();
        return;
      }

      this.productLoading = false;
      this.toastService.error('Failed to save product');
    });
  }

  deleteProduct(productId: number): void {
    this.productService.deleteProduct(productId).pipe(
      catchError(() => of(null)),
      takeUntil(this.destroy$)
    ).subscribe(result => {
      if (result === null) {
        this.toastService.error('Failed to delete product');
        return;
      }

      this.products = this.products.filter(product => product.id !== productId);
      this.toastService.info('Product removed');
    });
  }

  get externalLinkList(): string[] {
    return this.parseExternalLinks(this.user?.profile?.externalLinks || '');
  }

  get displayName(): string {
    return this.user?.profile?.fullName || this.user?.username || '';
  }

  get isPersonal(): boolean { return this.user?.role === 'PERSONAL'; }

  get isBusinessOrCreator(): boolean {
    return this.user?.role === 'BUSINESS' || this.user?.role === 'CREATOR';
  }

  get isPublicProfile(): boolean {
    return this.user?.profile?.privacy !== 'PRIVATE';
  }

  get tabs(): { key: ProfileTab; label: string }[] {
    return [
      { key: 'posts', label: `Posts (${this.posts.length})` },
      { key: 'followers', label: 'Followers' },
      { key: 'following', label: 'Following' }
    ];
  }

  private parseExternalLinks(value: string): string[] {
    return (value || '')
      .split(/\r?\n|,/)
      .map(link => link.trim())
      .filter(Boolean);
  }

  private serializeExternalLinks(value: string): string | undefined {
    const links = this.parseExternalLinks(value || '');
    return links.length ? links.join(',') : undefined;
  }

  private externalLinksText(value: string): string {
    return this.parseExternalLinks(value).join('\n');
  }
}
