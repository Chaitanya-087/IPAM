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
import {useLocation} from "react-router-dom";
import {Alert, Collapse, IconButton} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

const LoginPage = () => {
    const navigate = useNavigate();
    const {isLoading, setIsLoading, persistAuthState} = useAuth();
    const [showPassword, setShowPassword] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const {state} = useLocation();
    const [errorMessage, setErrorMessage] = useState("");
    const [isAlertOpen, setIsAlertOpen] = useState(false);

    const onChangeHandler = (setState) => (e) => {
        setErrorMessage("");
        setIsAlertOpen(false);
        setState(e.target.value);
    }

    const handleSubmit = async (e) => {
        try {
            e.preventDefault();
            if (username === "" || password === "") {
                return;
            }
            setIsLoading(true);
            await login(username, password);

            setPassword("");
            setUsername("");
        } catch (error) {
            setIsLoading(false);
            setIsAlertOpen(true);
            if (error.response.status === 401) {
                setErrorMessage("Invalid username or password");
            }
        }
    };

    const login = async (username, password) => {
        const res = await instance.post("/api/auth/token", {username, password});
        if (res.status === 200) {
            persistAuthState(res.data);
            setIsLoading(false);
            navigate(state?.from ? state.from : "/");
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
                        <Collapse in={isAlertOpen}>
                            <Alert
                                severity='error'
                                action={
                                    <IconButton
                                        aria-label='close'
                                        color='inherit'
                                        size='small'
                                        onClick={() => {
                                            setIsAlertOpen(false);
                                        }}>
                                        <CloseIcon fontSize='inherit' />
                                    </IconButton>
                                }
                                sx={{mb: 2}}>
                                {errorMessage}
                            </Alert>
                        </Collapse>
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
                                    onChange={onChangeHandler(setUsername)}
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
                                    onChange={onChangeHandler(setPassword)}
                                    id='password'
                                />
                                {!showPassword ? (
                                    <VisibilityOffIcon className='pwd-icon' onClick={toggleShowPassword} />
                                ) : (
                                    <VisibilityIcon className='pwd-icon' onClick={toggleShowPassword} />
                                )}
                            </div>
                        </div>
                        <button className='auth-btn' type='submit' id='submit'>
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
