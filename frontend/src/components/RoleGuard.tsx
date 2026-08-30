/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
import { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext';

interface RoleGuardProps {
  children: ReactNode;
  roles: string[];
  fallback?: ReactNode;
}

export default function RoleGuard({ children, roles, fallback = null }: RoleGuardProps) {
  const { hasRole } = useAuth();

  if (!hasRole(...roles)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
}
