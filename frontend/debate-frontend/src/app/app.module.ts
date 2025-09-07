import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './auth/login.component';
import { RegisterComponent } from './auth/register.component';
import { ProfileComponent } from './profile/profile.component';
import { DebateFormComponent } from './debates/debate-form.component';
import { DebateDetailComponent } from './debates/debate-detail.component';
import { AccountSettingsComponent } from './account/account-settings.component';

import { HeaderComponent } from './shared/header.component';
import { AuthInterceptor } from './core/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    DebateFormComponent,
    DebateDetailComponent,
    AccountSettingsComponent
  ],
  imports: [
    BrowserModule,           // provides CommonModule (date pipe), etc.
    ReactiveFormsModule,     // <-- needed for [formControl]
    HttpClientModule,
    AppRoutingModule         // exports RouterModule (routerLink)
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, multi: true, useClass: AuthInterceptor }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
