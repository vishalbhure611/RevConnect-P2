import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { UserRole } from '../../../../shared/models/models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  form: FormGroup;
  loading = false;
  error = '';
  showPassword = false;
  selectedRole: UserRole = 'PERSONAL';

  // ✅ MATCHED WITH HTML
  readonly roles: { value: UserRole; name: string; icon: string; desc: string }[] = [
    { value: 'PERSONAL', name: 'Personal', icon: '👤', desc: 'Individual profile' },
    { value: 'CREATOR', name: 'Creator', icon: '🎨', desc: 'Content & audience' },
    { value: 'BUSINESS', name: 'Business', icon: '🏢', desc: 'Company & brand' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['PERSONAL', Validators.required]
    });
  }

  selectRole(role: UserRole): void {
    this.selectedRole = role;
    this.form.patchValue({ role });
  }

  submit(): void {
    if (this.form.invalid || this.loading) return;

    this.loading = true;
    this.error = '';

    this.authService.register(this.form.value).subscribe({
      next: () => {
        this.router.navigate(['/auth/login'], {
          queryParams: { registered: 'true' }
        });
      },
      error: (err) => {
        this.error = err?.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }

  // ✅ IMPORTANT FIX (used in HTML)
  fieldError(field: string): string | null {
    const control = this.form.get(field);
    if (!control || !control.touched || !control.errors) return null;

    if (control.errors['required']) return 'This field is required';
    if (control.errors['email']) return 'Invalid email';
    if (control.errors['minlength']) return `Minimum ${control.errors['minlength'].requiredLength} characters required`;
    if (control.errors['maxlength']) return `Maximum ${control.errors['maxlength'].requiredLength} characters allowed`;

    return null;
  }

  // getters (optional but clean)
  get username() { return this.form.get('username'); }
  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }
}