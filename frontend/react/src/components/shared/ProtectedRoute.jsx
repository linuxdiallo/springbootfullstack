import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

const ProtectedRoute = ({ children }) => {

    // This Protected function is executed when user try to access
    // routes that are protected by it or when useAuth context that it uses did change
    const {isCustomerAuthenticated} = useAuth();

    const navigate = useNavigate();

    useEffect(() => {
        if(!isCustomerAuthenticated()) {
          navigate("/")
        }
    })

    return isCustomerAuthenticated() ? children : "";
}

export default ProtectedRoute;