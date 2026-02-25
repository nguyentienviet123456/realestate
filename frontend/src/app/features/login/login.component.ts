import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginForm: FormGroup;
  usernameError = '';
  passwordError = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.usernameError = '';
    this.passwordError = '';

    const { username, password } = this.loginForm.value;

    this.apiService.login(username, password).subscribe({
      next: (response) => {
        this.isLoading = false;

        if (response.success) {
          this.authService.login(username, password);
          this.router.navigate(['/']);
        } else if (response.errorCode === 'MSG-LOGIN-INFOR-NOT-ACTIVE') {
          this.usernameError = 'MSG-LOGIN-INFOR-NOT-ACTIVE';
        } else {
          this.usernameError = 'MSG-LOGIN-INFOR-ERROR';
          this.passwordError = 'MSG-LOGIN-INFOR-ERROR';
        }
      },
      error: () => {
        this.isLoading = false;
        this.usernameError = 'サーバーに接続できません。';
      },
    });
  }
}
