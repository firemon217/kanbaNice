import { useEffect } from "react";
import { useUser } from '../context/UserContext';
import { useNavigate } from "react-router-dom";

export default function OAuthRedirect() {
    const { syncToken } = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        
        const token = params.get("token");
        const userId = params.get("userId");
        const error = params.get("error");

        if (error) {
            console.error("OAuth Error:", error);
            navigate("/login?error=oauth_failed");
            return;
        }

        if (token) {
            localStorage.setItem("accessToken", token);
            syncToken();
            navigate("/profile"); 
        } else {
            console.warn("No token found in URL");
            navigate("/login");
        }
    }, [navigate]);

    return (
        <div className="flex items-center justify-center h-screen">
            <p>Logging you in...</p>
        </div>
    );
}