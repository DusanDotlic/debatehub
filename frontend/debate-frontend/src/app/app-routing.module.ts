import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { RegisterComponent } from './auth/register.component';
import { ProfileComponent } from './profile/profile.component';
import { DebateFormComponent } from './debates/debate-form.component';
import { DebateDetailComponent } from './debates/debate-detail.component';
import { AccountSettingsComponent } from './account/account-settings.component';
import { AuthGuard } from './core/auth.guard';
import { GuestGuard } from './core/guest.guard';

const routes: Routes = [
  { path: '', redirectTo: 'profile', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'debates/new', component: DebateFormComponent, canActivate: [AuthGuard] },
  { path: 'debates/:slug', component: DebateDetailComponent, canActivate: [AuthGuard] },
  { path: 'debates/:slug/edit', component: DebateFormComponent, canActivate: [AuthGuard] },

  { path: 'account', component: AccountSettingsComponent, canActivate: [AuthGuard] },
  // Future:
  // { path: 'join/:code', component: JoinViaCodeComponent },
  // { path: 'debates/:id/live', component: DebateLiveComponent },
  { path: '**', redirectTo: 'profile' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
