import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import 'bootstrap/dist/css/bootstrap.min.css';
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./ProtectedRoute";
import Main from "./pages/Main";
import KakaoAuthRedirect from "./pages/KakaoAuthRedirect";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    console.log('token=', token)
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = async (e) => {
    e.preventDefault();
    try {
      localStorage.removeItem('token');
      setIsLoggedIn(false)
    } catch (error) {
      alert('로그아웃 실패')
    }
  };

  return (
      <Router>
        <div>
          {/* 상단 네비게이션 바 */}
          <nav className="navbar navbar-expand-lg navbar-light bg-light">
            <div className="container">
              <Link className="navbar-brand" to="/">StreamFix</Link>
              <div className="collapse navbar-collapse">
                <ul className="navbar-nav ms-auto">
                  {
                    !isLoggedIn ? (
                        <>
                          <li className="nav-item">
                            <Link className="nav-link" to="/login">로그인</Link>
                          </li>
                          <li className="nav-item">
                            <Link className="nav-link" to="/signup">회원가입</Link>
                          </li>
                        </>
                    ) : (
                        <>
                          <li className="nav-item">
                            <button className="btn btn-danger" onClick={handleLogout}>로그아웃</button>
                          </li>
                        </>
                    )
                  }
                </ul>
              </div>
            </div>
          </nav>

          {/* 페이지 라우팅 */}
          <div className="container mt-5">
            <Routes>
              <Route path="/" element={<Main/>}/>
              <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn}/>}/>
              <Route path="/signup" element={<Signup/>}/>
              <Route path="/login/oauth2/code/kakao" element={<KakaoAuthRedirect/>}/>

              <Route path="/dashboard" element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }/>
            </Routes>
          </div>
        </div>
      </Router>
  );
}

export default App;