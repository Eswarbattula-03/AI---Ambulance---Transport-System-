import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Ambulance, LogOut, User as UserIcon, Activity } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-red-700 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-3">
            <Ambulance className="w-8 h-8 text-yellow-300 animate-pulse" />
            <Link to="/" className="text-xl font-bold tracking-wider hover:text-yellow-200 flex items-center gap-2">
              <span>LifeRescue</span>
              <span className="text-xs bg-red-900 text-yellow-300 px-2 py-0.5 rounded-full font-mono">AI SOS</span>
            </Link>
          </div>

          {user && (
            <div className="flex items-center space-x-6">
              <div className="flex items-center space-x-2 text-sm bg-red-800 px-3 py-1.5 rounded-lg border border-red-600">
                <UserIcon className="w-4 h-4 text-yellow-300" />
                <span className="font-medium">{user.name}</span>
                <span className="text-xs text-yellow-200 font-semibold uppercase bg-red-950 px-2 py-0.5 rounded">
                  {user.role}
                </span>
              </div>

              <button
                onClick={handleLogout}
                className="flex items-center space-x-1 text-sm bg-red-900 hover:bg-red-950 px-3 py-1.5 rounded-lg border border-red-700 transition"
              >
                <LogOut className="w-4 h-4" />
                <span>Logout</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};
