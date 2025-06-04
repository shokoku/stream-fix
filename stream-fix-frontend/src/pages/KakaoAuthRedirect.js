import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function KakaoAuthRedirect() {
  const navigate = useNavigate();

  useEffect(() => {
    const code = new URL(window.location.href).searchParams.get("code");

    if (code) {
      axios.post('http://localhost:8080/api/v1/user/callback', { code })
      .then(response => {
        console.log(response)
        const token = response.data.data.accessToken;
        localStorage.setItem('token', token);
        navigate('/dashboard');
      })
      .catch(error => {
        console.error('카카오 로그인 실패:', error);
      });
    }
  }, [navigate]);

  return (
      <div>
        카카오 로그인 처리 중...
      </div>
  );
}

export default KakaoAuthRedirect;