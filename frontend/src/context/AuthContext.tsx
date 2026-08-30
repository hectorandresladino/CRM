/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  role: string | null;
  tenantId: number | null;
  username: string | null;
}

interface AuthContextType extends AuthState {
  login: (accessToken: string, refreshToken: string, role: string, tenantId: number | null, username: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (...roles: string[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [authState, setAuthState] = useState<AuthState>({
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
    role: localStorage.getItem('role'),
    tenantId: localStorage.getItem('tenantId') ? Number(localStorage.getItem('tenantId')) : null,
    username: localStorage.getItem('username'),
  });

  const login = (accessToken: string, refreshToken: string, role: string, tenantId: number | null, username: string) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('role', role);
    localStorage.setItem('tenantId', String(tenantId));
    localStorage.setItem('username', username);
    setAuthState({ accessToken, refreshToken, role, tenantId, username });
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('role');
    localStorage.removeItem('tenantId');
    localStorage.removeItem('username');
    localStorage.removeItem('usuario');
    setAuthState({ accessToken: null, refreshToken: null, role: null, tenantId: null, username: null });
  };

  const isAuthenticated = !!authState.accessToken;

  const hasRole = (...roles: string[]) => {
    if (!authState.role) return false;
    return roles.includes(authState.role);
  };

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      setAuthState((prev) => ({
        ...prev,
        accessToken: token,
        refreshToken: localStorage.getItem('refreshToken'),
        role: localStorage.getItem('role'),
        tenantId: localStorage.getItem('tenantId') ? Number(localStorage.getItem('tenantId')) : null,
        username: localStorage.getItem('username'),
      }));
    }
  }, []);

  return (
    <AuthContext.Provider value={{ ...authState, login, logout, isAuthenticated, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de AuthProvider');
  }
  return context;
}
