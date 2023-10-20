/* eslint-disable react/no-unescaped-entities */
import {Link, useNavigate} from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import {useState} from "react";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import {BarLoader} from "react-spinners";
import "../styles/auth.css";
import useAuth from "../hooks/useAuth";
import instance from "../api/axios";

const LoginPage = () => {
    const navigate = useNavigate();
    const {isLoading, setIsLoading, persistAuthState} = useAuth();
    const [showPassword, setShowPassword] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (username === "" || password === "") {
            return;
        }
        setIsLoading(true);
        const res = await instance.post("/api/auth/token", {username, password});
        setIsLoading(false);
        setPassword("");
        setUsername("");
        if (!isLoading && res.status === 200) {
            persistAuthState(res.data);
            navigate("/");
        }
    };

    const toggleShowPassword = () => {
        setShowPassword(!showPassword);
    };

    return (
        <section className='auth'>
            <div className='wrapper'>
                <button className='back-btn' onClick={() => navigate(-1)}>
                    <ArrowBackIcon className='back-icon' />
                </button>
                <div className='banner'>
                    <div className='circle'></div>
                    <div className='overlay'></div>
                </div>
                <div className='auth-body'>
                    <form className='auth-form' onSubmit={handleSubmit}>
                        <h3 className='auth-title'>Welcome back</h3>
                        <p className='auth-desc'>Welcome back! Please enter your details</p>
                        <div className='input-group'>
                            <label htmlFor='username' className='label'>
                                username
                            </label>
                            <div className='input-container' data-error='enter valid email'>
                                <input
                                    type='text'
                                    name='username'
                                    required
                                    placeholder='Enter username'
                                    autoComplete='username'
                                    autoFocus='on'
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    id='username'
                                />
                            </div>
                        </div>
                        <div className='input-group'>
                            <label htmlFor='password' className='label'>
                                Password
                            </label>
                            <div className='input-container'>
                                <input
                                    type={showPassword ? "text" : "password"}
                                    name='password'
                                    required
                                    placeholder='Enter your password'
                                    min='6'
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    id='current-password'
                                />
                                {!showPassword ? (
                                    <VisibilityOffIcon className='pwd-icon' onClick={toggleShowPassword} />
                                ) : (
                                    <VisibilityIcon className='pwd-icon' onClick={toggleShowPassword} />
                                )}
                            </div>
                        </div>
                        <button className='auth-btn' type='submit'>
                            {isLoading ? (
                                <div className='loader'>
                                    <BarLoader color='#fff' loading={isLoading} size={10} height={2} />
                                </div>
                            ) : (
                                "Sign in"
                            )}
                        </button>
                        <span className='message'>
                            Don't have an account?{" "}
                            <Link to='/sign-up' className='link-to'>
                                Sign up
                            </Link>
                        </span>
                    </form>
                </div>
            </div>
        </section>
    );
};

export default LoginPage;
