package com.tirsankardan.tirsanuygulama

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.tirsankardan.tirsanuygulama.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val usernameEditText = binding.usernameEditText
        val passwordEditText = binding.passwordEditText
        val loginButton = binding.loginButton

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username == "tirsan" && password == "Trs1957!") {
                sharedViewModel.isAdmin.value = true
                Navigation.findNavController(it).navigate(R.id.action_login_to_anasayfa)
            } else if (username != "tirsan" && password == "Trs1957!") {
                Toast.makeText(requireContext(), "Kullanıcı adı hatalı!", Toast.LENGTH_SHORT).show()
            } else if (username == "tirsan" && password != "Trs1957!") {
                Toast.makeText(requireContext(), "Şifre hatalı!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Kullanıcı adı ve şifre hatalı!", Toast.LENGTH_SHORT).show()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri düğmesine basıldığında ne yapılacağını burada belirtin
                // Örneğin, uygulamadan çıkmak istiyorsanız
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
        val sharedPreferences = activity?.getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("isLoggedIn", true)
        editor?.apply()

        val action = LoginFragmentDirections.actionLoginToAnasayfa()
        findNavController().navigate(action)

        parentFragmentManager.popBackStack()
    }
}